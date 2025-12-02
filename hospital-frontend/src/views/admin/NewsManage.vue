<!-- NewsManage.vue -->
<template>
  <div class="news-manage">
    <!-- 搜索和操作栏 -->
    <div class="search-bar">
        <el-input 
            v-model="searchQuery"
            placeholder="搜索资讯标题"
            style="width: 300px; margin-right: 20px;"
            @keyup.enter="loadNewsList">
            <i slot="prefix" class="el-icon-search"></i>
        </el-input>
  
        <el-select v-model="searchStatus" placeholder="状态" style="width: 120px; margin-right: 20px;">
            <el-option label="全部" value=""></el-option>
            <el-option label="草稿" value="2"></el-option>
            <el-option label="已发布" value="1"></el-option>
            <el-option label="已下架" value="0"></el-option>
        </el-select>
  
        <el-button type="primary" @click="loadNewsList">
            <i class="el-icon-search"></i> 搜索
        </el-button>
  
        <el-button type="info" @click="resetSearch">
            <i class="el-icon-refresh"></i> 重置
        </el-button>
  
        <el-button type="success" @click="showAddDialog">
            <i class="el-icon-plus"></i> 新增资讯
        </el-button>
    </div>
    
    <!-- 资讯列表 -->
    <el-table :data="newsList" border style="width: 100%">
      <el-table-column prop="newsId" label="ID" width="80"></el-table-column>
      <el-table-column label="标题" min-width="200">
        <template slot-scope="scope">
          <div class="title-cell">
            <span v-if="scope.row.isTop" class="top-tag">置顶</span>
            {{ scope.row.title }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="newsType" label="类型" width="100"></el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- <el-table-column prop="viewCount" label="浏览量" width="100"></el-table-column> -->
      <el-table-column prop="publishTime" label="发布时间" width="160"></el-table-column>
      <el-table-column label="封面图片" width="100">
        <template slot-scope="scope">
          <div v-if="scope.row.coverImage" class="cover-image-cell">
            <img :src="scope.row.coverImage" alt="封面" @click="previewImage(scope.row.coverImage)">
          </div>
          <span v-else style="color: #999;">无封面</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="340" fixed="right">
        <template slot-scope="scope">
        <!-- 添加包裹div，让按钮在同一行 -->
            <div class="operation-buttons" style="display: flex; gap: 5px;">
                <el-button size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
                <el-button 
                    size="mini" 
                    :type="scope.row.status === 1 ? 'warning' : 'success'"
                    @click="toggleStatus(scope.row)">
                     {{ scope.row.status === 1 ? '下架' : '发布' }}
                </el-button>
                <el-button 
                size="mini" 
                :type="scope.row.isTop === 1 ? 'info' : 'primary'"
                @click="toggleTop(scope.row)">
                {{ scope.row.isTop === 1 ? '取消置顶' : '置顶' }}
                </el-button>
                <el-button size="mini" type="danger" @click="deleteNews(scope.row.newsId)">删除</el-button>
            </div>
        </template>
     </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogVisible"
      width="60%"
      @close="resetForm">
      <el-form :model="form" :rules="rules" ref="newsForm" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入资讯标题"></el-input>
        </el-form-item>
        
        <el-form-item label="类型" prop="newsType">
          <el-select v-model="form.newsType" placeholder="请选择资讯类型">
            <el-option label="医院动态" value="医院动态"></el-option>
            <el-option label="健康科普" value="健康科普"></el-option>
            <el-option label="通知公告" value="通知公告"></el-option>
            <el-option label="医疗技术" value="医疗技术"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="封面图片">
          <div class="cover-image-upload">
            <!-- 图片预览 -->
            <div v-if="form.coverImage" class="image-preview">
              <img :src="form.coverImage" alt="封面预览" @click="previewImage(form.coverImage)">
              <div class="preview-actions">
                <el-button type="danger" size="mini" @click="removeCoverImage">删除</el-button>
              </div>
            </div>
            
            <!-- 上传组件 -->
            <el-upload
              class="cover-upload"
              action="/upload/image"
              :show-file-list="false"
              :on-success="handleCoverUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeCoverUpload"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <el-button type="primary" icon="el-icon-upload">上传封面</el-button>
              <div slot="tip" class="upload-tip">支持JPG/PNG/GIF格式，大小不超过2MB</div>
            </el-upload>
            
            <!-- URL输入框 -->
            <!-- <div class="url-input">
              <el-input 
                v-model="form.coverImage" 
                placeholder="或直接输入图片URL"
                size="small"
                style="margin-top: 10px;">
                <template slot="append">
                  <el-button @click="testImageUrl">测试</el-button>
                </template>
              </el-input>
            </div> -->
          </div>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <div class="content-editor">
            <el-input 
              type="textarea" 
              v-model="form.content" 
              :rows="10"
              placeholder="请输入资讯内容，支持HTML格式">
            </el-input>
            <div class="editor-tools">
              <el-upload
                class="editor-upload"
                action="/upload/editor"
                :show-file-list="false"
                :on-success="handleEditorUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeEditorUpload"
                accept=".jpg,.jpeg,.png,.gif"
              >
               
              </el-upload>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="2">草稿</el-radio>
            <el-radio :label="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="置顶" prop="isTop">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
    
    <!-- 图片预览弹窗 -->
    <el-dialog 
      :visible.sync="imagePreviewVisible" 
      title="图片预览"
      width="50%">
      <div class="image-preview-dialog">
        <img :src="previewImageUrl" alt="预览" style="max-width: 100%; max-height: 500px;">
      </div>
      <span slot="footer">
        <el-button @click="imagePreviewVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request.js";

export default {
  name: "NewsManage",
  data() {
    return {
      searchQuery: '',
      searchStatus: '',
      newsList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      
      dialogVisible: false,
      dialogTitle: '新增资讯',
      form: {
        newsId: null,
        title: '',
        content: '',
        coverImage: '',
        newsType: '医院动态',
        status: 2,
        isTop: 0
      },
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 3, max: 200, message: '长度在 3 到 200 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      },
      
      // 图片预览
      imagePreviewVisible: false,
      previewImageUrl: ''
    };
  },
  mounted() {
    this.loadNewsList();
  },
  methods: {
    loadNewsList() {
      request.get('admin/news/list', {
        params: {
          pageNumber: this.currentPage,
          size: this.pageSize,
          query: this.searchQuery,
          status: this.searchStatus
        }
      }).then(res => {
        if (res.data.status === 200) {
          this.newsList = res.data.data.records;
          this.total = res.data.data.total;
        }
      });
    },

    // 重置搜索
    resetSearch() {
        this.searchQuery = '';
        this.searchStatus = '';
        this.currentPage = 1;
        this.loadNewsList();
        this.$message.success('搜索条件已重置');
    },
  
    handleSizeChange(val) {
        this.pageSize = val;
        this.loadNewsList();
    },
    
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadNewsList();
    },
    
    showAddDialog() {
      this.dialogTitle = '新增资讯';
      this.form = {
        newsId: null,
        title: '',
        content: '',
        coverImage: '',
        newsType: '医院动态',
        status: 2,
        isTop: 0
      };
      this.dialogVisible = true;
    },
    
    showEditDialog(row) {
      this.dialogTitle = '编辑资讯';
      this.form = { ...row };
      this.dialogVisible = true;
    },
    
    submitForm() {
      this.$refs.newsForm.validate(valid => {
        if (!valid) return;
        
        if (this.form.newsId) {
          // 编辑
          request.post('admin/news/update', this.form)
            .then(res => {
              if (res.data.status === 200) {
                this.$message.success('修改成功');
                this.dialogVisible = false;
                this.loadNewsList();
              } else {
                this.$message.error('修改失败');
              }
            });
        } else {
          // 新增
          request.post('admin/news/add', this.form)
            .then(res => {
              if (res.data.status === 200) {
                this.$message.success('添加成功');
                this.dialogVisible = false;
                this.loadNewsList();
              } else {
                this.$message.error('添加失败');
              }
            });
        }
      });
    },
    
    toggleStatus(row) {
        const newStatus = row.status === 1 ? 0 : 1;
        const action = newStatus === 1 ? '发布' : '下架';
  
        request.post('admin/news/updateStatus', null, {
            params: {
                newsId: row.newsId,
                status: newStatus
            }
        }).then(res => {
        if (res.data.status === 200) {
            this.$message.success(`${action}成功！`);
            this.loadNewsList();
        }
        });
    },
    
    toggleTop(row) {
        const newTop = row.isTop === 1 ? 0 : 1;
        const action = newTop === 1 ? '置顶' : '取消置顶';
  
        request.post('admin/news/updateTop', null, {
            params: {
                newsId: row.newsId,
                isTop: newTop
            }
        }).then(res => {
            if (res.data.status === 200) {
                this.$message.success(`${action}成功！`);
                this.loadNewsList();
            } else {
                this.$message.error('操作失败');
            }
        }).catch(error => {
            console.error('置顶操作失败:', error);
            this.$message.error('操作失败，请重试');
        });
    },
    
    deleteNews(newsId) {
      this.$confirm('确定删除该资讯吗？', '提示', {
        type: 'warning'
      }).then(() => {
        request.post('admin/news/delete', null, {
          params: { newsId }
        }).then(res => {
          if (res.data.status === 200) {
            this.$message.success('资讯删除成功！');
            this.loadNewsList();
          }
        });
      });
    },
    
    getStatusText(status) {
      const map = { 0: '已下架', 1: '已发布', 2: '草稿' };
      return map[status] || '未知';
    },
    
    getStatusTagType(status) {
      const map = { 0: 'danger', 1: 'success', 2: 'info' };
      return map[status] || '';
    },
    
    resetForm() {
      this.$refs.newsForm.resetFields();
    },
    
    // ========== 图片上传相关方法 ==========
    // 封面图片上传前验证
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      
      if (!isImage) {
        this.$message.error('只能上传图片文件！');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过2MB！');
        return false;
      }
      return true;
    },
    
    // 编辑器图片上传前验证
    beforeEditorUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt5M = file.size / 1024 / 1024 < 5;
      
      if (!isImage) {
        this.$message.error('只能上传图片文件！');
        return false;
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB！');
        return false;
      }
      return true;
    },
    
    // 封面图片上传成功
    handleCoverUploadSuccess(res, file, fileList) {
    console.log('上传响应:', res);
    
    if (res && res.status === 200) {
        // 获取图片URL
        let imageUrl = '';
        if (res.data && res.data.fileUrl) {
            imageUrl = res.data.fileUrl;
        } else if (res.data && res.data.url) {
            imageUrl = res.data.url;
        } else if (typeof res.data === 'string') {
            imageUrl = res.data;
        }
        
        if (imageUrl) {
            this.form.coverImage = imageUrl;
            this.$message.success(res.msg || '上传成功');
            console.log('设置的图片URL:', imageUrl);
        } else {
            console.warn('无法从响应中获取图片URL:', res);
            this.$message.error('上传成功但无法获取图片地址');
        }
    } else {
        this.$message.error('上传失败: ' + (res.msg || res.message || '未知错误'));
    }
},

// 编辑器图片上传成功
handleEditorUploadSuccess(res, file, fileList) {
    console.log('编辑器上传响应:', res);
    
    if (res && res.status === 200) {
        // 获取图片URL
        let imageUrl = '';
        if (res.data && res.data.fileUrl) {
            imageUrl = res.data.fileUrl;
        } else if (res.data && res.data.url) {
            imageUrl = res.data.url;
        } else if (typeof res.data === 'string') {
            imageUrl = res.data;
        }
        
        if (imageUrl) {
            // 插入图片到内容中
            const imageTag = `<img src="${imageUrl}" alt="图片" style="max-width: 100%; margin: 10px 0;">`;
            this.form.content += imageTag + '\n';
            this.$message.success('图片上传成功，已插入到内容中');
        } else {
            this.$message.error('上传成功但无法获取图片地址');
        }
    } else {
        this.$message.error('上传失败: ' + (res.msg || res.message || '未知错误'));
    }
},
    
    // 上传失败处理
    handleUploadError(err) {
      console.error('上传失败:', err);
      this.$message.error('图片上传失败，请重试');
    },
    
    // 删除封面图片
    removeCoverImage() {
      this.form.coverImage = '';
      this.$message.info('封面图片已清除');
    },
    
    // 测试图片URL
    testImageUrl() {
      if (!this.form.coverImage) {
        this.$message.warning('请输入图片URL');
        return;
      }
      
      const img = new Image();
      img.onload = () => {
        this.$message.success('图片URL有效');
      };
      img.onerror = () => {
        this.$message.error('图片URL无效或无法访问');
      };
      img.src = this.form.coverImage;
    },
    
    // 预览图片
    previewImage(url) {
      this.previewImageUrl = url;
      this.imagePreviewVisible = true;
    }
  }
};
</script>

<style scoped>
.news-manage {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.title-cell {
  display: flex;
  align-items: center;
}

.top-tag {
  display: inline-block;
  background: #ff9800;
  color: white;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
  margin-right: 8px;
}

.cover-image-cell {
  width: 50px;
  height: 50px;
  cursor: pointer;
}

.cover-image-cell img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.cover-image-cell img:hover {
  transform: scale(1.05);
  transition: transform 0.3s;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

/* 封面图片上传样式 */
.cover-image-upload {
  width: 100%;
}

.image-preview {
  margin-bottom: 15px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 10px;
  text-align: center;
  background: #fafafa;
}

.image-preview img {
  max-width: 200px;
  max-height: 150px;
  border-radius: 4px;
  cursor: pointer;
}

.image-preview img:hover {
  opacity: 0.9;
}

.preview-actions {
  margin-top: 10px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 内容编辑器样式 */
.content-editor {
  width: 100%;
}

.editor-tools {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.editor-tip {
  font-size: 12px;
  color: #999;
}

/* 图片预览弹窗 */
.image-preview-dialog {
  text-align: center;
}

.image-preview-dialog img {
  max-width: 100%;
  max-height: 500px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>