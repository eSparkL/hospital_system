<!--
 * 登录页
-->
<template>
    <div class="login-index" :style="backgroundDiv">
        <!-- 侧边栏切换按钮 -->
        <div class="sidebar-toggle" @click="toggleSidebar">
            <i :class="isSidebarCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
        </div>

        <!-- 资讯侧边栏 -->
        <div class="news-sidebar" :class="{ collapsed: isSidebarCollapsed }" v-if="showNews">
            <div class="news-sidebar-container">
                <div class="news-header">
                    <i class="iconfont icon-news"></i>
                    <span class="news-title-text" v-show="!isSidebarCollapsed">医院资讯</span>
                </div>
                
                <!-- 加载状态 -->
                <div v-if="loading && !isSidebarCollapsed" class="loading-container">
                    <i class="el-icon-loading"></i>
                    <span>正在加载资讯...</span>
                </div>
                
                <!-- 无数据提示 -->
                <div v-else-if="topNews.length === 0 && latestNews.length === 0 && !isSidebarCollapsed" class="empty-news">
                    <i class="el-icon-news"></i>
                    <p>暂无资讯</p>
                </div>
                
                <!-- 资讯内容 -->
                <div v-else class="news-content" :class="{ collapsed: isSidebarCollapsed }">
                    <!-- 收起时的简化显示 -->
                    <div v-if="isSidebarCollapsed" class="collapsed-view">
                        <div class="collapsed-count" @click="toggleSidebar">
                            <i class="el-icon-s-unfold"></i>
                            <span class="count-badge" v-if="topNews.length > 0">{{ topNews.length }}</span>
                        </div>
                        <div class="collapsed-preview" @click="toggleSidebar">
                            <i class="el-icon-news"></i>
                            <span>资讯</span>
                        </div>
                    </div>
                    
                    <!-- 展开时的完整显示 -->
                    <div v-else class="expanded-content">
                        <!-- 置顶资讯 -->
                        <div class="top-news-section" v-if="topNews.length > 0">
                            <div class="section-title">
                                <i class="el-icon-star-on"></i>
                                <span>置顶资讯</span>
                            </div>
                            <div class="top-news-list scrollable-container">
                                <div class="top-news-item" 
                                     v-for="item in topNews" 
                                     :key="item.newsId"
                                     @click="viewNewsDetail(item.newsId, item.coverImage)">
                                    <!-- 添加封面图片显示 -->
                                    <div class="news-cover" v-if="item.coverImage">
                                        <img :src="item.coverImage" alt="封面" @click.stop="previewCoverImage(item.coverImage)">
                                    </div>
                                    <div class="top-tag">置顶</div>
                                    <div class="news-title">{{ item.title }}</div>
                                    <div class="news-info">
                                        <span class="news-type">{{ item.newsType }}</span>
                                        <span class="news-time">{{ formatTime(item.publishTime) }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- 最新资讯 -->
                        <div class="latest-news-section">
                            <div class="section-title">
                                <i class="el-icon-time"></i>
                                <span>最新资讯</span>
                            </div>
                            <div class="latest-news-list scrollable-container">
                                <div class="latest-news-item" 
                                     v-for="item in latestNews" 
                                     :key="item.newsId"
                                     @click="viewNewsDetail(item.newsId, item.coverImage)">
                                    <!-- 添加封面图片显示 -->
                                    <div class="news-cover" v-if="item.coverImage">
                                        <img :src="item.coverImage" alt="封面" @click.stop="previewCoverImage(item.coverImage)">
                                    </div>
                                    <div class="item-content">
                                        <div class="news-title">{{ item.title }}</div>
                                        <div class="news-meta">
                                            <span class="news-type">{{ item.newsType }}</span>
                                            <span class="news-time">{{ formatTime(item.publishTime) }}</span>
                                            <!-- <span class="news-views">浏览: {{ item.viewCount || 0 }}</span> -->
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 登录窗口 -->
        <div class="login-window-index">
            <!-- 登录标题 -->
            <i style=" top: 40px; font-size: 28px; left: 70px;position: absolute;"class="iconfont">
              <span style="color: #e75c09;" >登录医院管理系统</span>
            </i>

            <!-- 登录表单 -->
            <el-form
                :model="loginForm"
                :rules="loginRules"
                ref="ruleForm"
                class="loginForm"
            >
                <!-- 用户名 -->
                <el-form-item prop="id">
                    <el-input v-model="loginForm.id">
                        <i  slot="prefix"
                            class="iconfont icon-login-user"
                            style="font-size: 22px"
                        ></i>
                    </el-input>
                </el-form-item>

              <!-- 密码 -->
                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" show-password>
                        <i
                            slot="prefix"
                            class="iconfont icon-login-password"
                            style="font-size: 22px"
                        ></i>
                    </el-input>
                </el-form-item>

                <!-- 角色登录选择框 -->
                <el-form-item class="role_radio">
                    <el-radio-group v-model="role" style="margin-left: 1px" size="small">
                        <el-radio-button label="患者"></el-radio-button>
                        <el-radio-button label="医生"></el-radio-button>
                        <el-radio-button label="管理员"></el-radio-button>
                    </el-radio-group>
                </el-form-item>

                <el-form-item class="login-buttons">
                    <el-button
                        type="success"
                        style="font-size: 18px"
                        @click="submitLoginForm('ruleForm')"
                    >
                        <i class="iconfont icon-login-button"
                           style="font-size: 20px">
                        </i>
                        登录
                    </el-button>
                    <el-button
                        type="info"
                        style="font-size: 18px"
                        @click="registerFormVisible = true"
                    >
                        <i class="iconfont icon-login-register"
                           style="font-size: 20px">
                        </i>
                        注册
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 注册对话框 -->
        <el-dialog title="用户注册" :visible.sync="registerFormVisible" >
            <el-form
                :model="registerForm"
                :rules="registerRules"
                ref="registerForm"
            >
                <el-form-item label="账号" label-width="80px" prop="pId">
                    <el-input v-model.number="registerForm.pId"></el-input>
                </el-form-item>

                <el-form-item label="密码" label-width="80px" prop="pPassword">
                  <el-input v-model="registerForm.pPassword"></el-input>
                </el-form-item>

                <el-form-item label="姓名" label-width="80px" prop="pName">
                    <el-input v-model="registerForm.pName"></el-input>
                </el-form-item>

                <el-form-item label="性别" label-width="80px">
                <el-radio v-model="registerForm.pGender" label="男">
                  男
                </el-radio>
                <el-radio v-model="registerForm.pGender" label="女">
                  女
                </el-radio>
              </el-form-item>

                <el-form-item
                    label="出生日期"
                    label-width="80px"
                    prop="pBirthday"
                >
                    <el-date-picker
                        v-model="registerForm.pBirthday"
                        type="date"
                        placeholder="选择日期"
                        value-format="yyyy-MM-dd"
                    >
                    </el-date-picker>
                </el-form-item>

                <el-form-item label="手机号" label-width="80px" prop="pPhone">
                    <el-input v-model="registerForm.pPhone"></el-input>
                </el-form-item>

                <el-form-item label="邮箱" label-width="80px" prop="pEmail">
                    <el-input v-model="registerForm.pEmail"></el-input>
                </el-form-item>

                <el-form-item label="身份证号" label-width="80px" prop="pCard">
                    <el-input v-model="registerForm.pCard"></el-input>
                </el-form-item>

            </el-form>

            <div slot="footer">
                <el-button
                    @click="registerFormVisible = false"
                    style="font-size: 18px"
                    >
                   <i class="iconfont icon-cancel-button"style="font-size: 20px"></i>
                    取 消
                </el-button>
                <el-button
                    type="primary"
                    @click="register('registerForm')"
                    style="font-size: 18px"
                  >
                  <i class="iconfont icon-sure-button" style="font-size: 20px"></i>
                    确 定
                </el-button>
            </div>
        </el-dialog>

        <!-- 资讯详情弹窗（合并图片展示） -->
        <el-dialog 
            :title="currentNews ? currentNews.title : '资讯详情'" 
            :visible.sync="newsDetailVisible"
            width="80%"
            top="3vh"
            custom-class="news-detail-dialog"
            @close="newsDetailVisible = false">
            <div class="news-detail" v-if="currentNews">
                <!-- 封面图片大图展示区域 -->
                <div class="news-cover-fullscreen" v-if="currentNews.coverImage">
                    <img :src="currentNews.coverImage" alt="封面" class="fullscreen-cover-image">
                </div>
                
                <div class="news-detail-main">
                    <div class="news-detail-header">
                        <h2 class="news-detail-title">{{ currentNews.title }}</h2>
                        <div class="news-detail-meta">
                            <span class="meta-item">
                                <i class="el-icon-price-tag"></i>
                                类型: {{ currentNews.newsType }}
                            </span>
                            <span class="meta-item">
                                <i class="el-icon-time"></i>
                                发布时间: {{ formatDetailTime(currentNews.publishTime) }}
                            </span>
                            <!-- <span class="meta-item">
                                <i class="el-icon-view"></i>
                                浏览量: {{ currentNews.viewCount || 0 }}
                            </span> -->
                        </div>
                    </div>
                    <div class="news-detail-content-wrapper">
                        <div class="news-detail-content" v-html="currentNews.content"></div>
                    </div>
                </div>
            </div>
            <!-- <span slot="footer">
                <el-button @click="newsDetailVisible = false">关闭</el-button>
            </span> -->
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
export default {
    name: "Login",
    data() {
        var validatePhone = (rule, value, callback) => {
            if (value === undefined) {
                callback(new Error("请输入手机号"));
            } else {
                let reg =/^1(3[0-9]|4[5,7]|5[0,1,2,3,5,6,7,8,9]|6[2,5,6,7]|7[0,1,7,8]|8[0-9]|9[1,8,9])\d{8}$/;
                if (!reg.test(value)) {
                    callback(new Error("请输入合法的手机号"));
                }
                callback();
            }
        };
        var validateCard = (rule, value, callback) => {
            if (value === undefined) {
                callback(new Error("请输入身份证号"));
            } else {
                let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
                if (!reg.test(value)) {
                    callback(new Error("请输入合法的身份证号码"));
                }
                callback();
            }
        };
        let validatePass = (rule, value, callback) => {
            if (value === "") {
                callback(new Error("请输入密码"));
            } else {
                if (this.findForm.checkPassword !== "") {
                    this.$refs.findForm.validateField("checkPassword");
                }
                callback();
            }
        };
       let validatePassAgain = (rule, value, callback) => {
            if (value === "") {
                callback(new Error("请再次输入密码"));
            } else if (value !== this.findForm.newPassword) {
                callback(new Error("两次输入密码不一致!"));
            } else {
                callback();
            }
        };
        return {
            // 背景图片
            backgroundDiv: {
                backgroundImage: "url(" + require("../assets/background.png") + ")",
                backgroundRepeat: "no-repeat",
                backgroundSize: "100% 100%",
            },
            loginForm: {
                id: "",
                password: "",
            },
            loginRules: {
                id: [
                    {
                        required: true,
                        message: "请输入账号编号",
                        trigger: "blur",
                    },
                    {
                        min: 3,
                        max: 50,
                        message: "长度在 3到 50 个字符",
                        trigger: "blur",
                    },
                ],
                password: [
                    { required: true, message: "请输入密码", trigger: "blur" },
                ],
            },
            role: "患者",
            findRole: "患者",
            // 找回密码
            findFormVisible: false,
            findForm: {
                code: "",
                newPassword: "",
                checkPassword: "",
                pEmail: "",
            },

            findRules: {
                pEmail: [
                    {
                        required: true,
                        message: "请输入邮箱地址",
                        trigger: "blur",
                    },
                    {
                        type: "email",
                        message: "请输入正确的邮箱地址",
                        trigger: ["blur", "change"],
                    },
                ],
                code: [
                    {
                        required: true,
                        message: "请输入验证码",
                        trigger: "blur",
                    },
                ],
                newPassword: [{ validator: validatePass, trigger: "blur" }],
                checkPassword: [{ validator: validatePassAgain, trigger: "blur" }],
            },
            totalTime: 60,
            content: "发送验证码",
            canClick: true,
            // 注册
            registerFormVisible: false,
            registerForm: {
                pGender: "男",
            },
            registerRules: {
                pId: [
                    { required: true, message: "请输入账号", trigger: "blur" },
                    {
                        type: "number",
                        message: "账号必须数字类型",
                        trigger: "blur",
                    },
                ],
                pPassword: [
                    { required: true, message: "请输入密码", trigger: "blur" },
                    {
                        min: 4,
                        max: 50,
                        message: "长度在 4到 50 个字符",
                        trigger: "blur",
                    },
                ],
                pName: [
                    { required: true, message: "请输入姓名", trigger: "blur" },
                    {
                        min: 2,
                        max: 8,
                        message: "长度在 2到 8 个字符",
                        trigger: "blur",
                    },
                ],
                pEmail: [
                    { required: true, message: "请输入邮箱", trigger: "blur" },
                    {
                        type: "email",
                        message: "请输入正确的邮箱地址",
                        trigger: ["blur", "change"],
                    },
                ],
                pPhone: [{ validator: validatePhone }],
                pCard: [{ validator: validateCard }],
                pBirthday: [
                    {
                        required: true,
                        message: "选择出生日期",
                        trigger: "blur",
                    },
                ],
            },
            
            // 新增资讯相关数据
            showNews: true, // 控制是否显示资讯
            loading: false,
            topNews: [],
            latestNews: [],
            newsDetailVisible: false,
            currentNews: null,
            
            // 侧边栏状态
            isSidebarCollapsed: false,
        };
    },
    mounted() {
        this.loadNews();
    },
    methods: {
        // 切换侧边栏状态
        toggleSidebar() {
            this.isSidebarCollapsed = !this.isSidebarCollapsed;
        },
        
        // 加载资讯
        loadNews() {
            this.loading = true;
    
            // 同时请求置顶资讯和最新资讯
            Promise.all([
                request.get("news/top", { params: { limit: 3 } }),
                request.get("news/latest", { params: { limit: 8 } })
            ])
            .then(([topRes, latestRes]) => {
                // 处理置顶资讯
                if (topRes.data && topRes.data.status === 200) {
                    this.topNews = topRes.data.data || [];
                } else {
                    this.topNews = [];
                }
            
                // 处理最新资讯
                if (latestRes.data && latestRes.data.status === 200) {
                    this.latestNews = latestRes.data.data || [];
                } else {
                    this.latestNews = [];
                }
            })
            .catch(error => {
                console.error('加载资讯失败:', error);
                this.topNews = [];
                this.latestNews = [];
            })
            .finally(() => {
                this.loading = false;
            });
        },
        
        // 查看资讯详情（现在包含封面图片）
        viewNewsDetail(newsId, coverImage) {
            console.log('查看资讯详情:', newsId);
            request.get(`news/detail/${newsId}`)
                .then(res => {
                    console.log('资讯详情响应:', res);
                    if (res.data && res.data.status === 200) {
                        this.currentNews = res.data.data;
                        this.newsDetailVisible = true;
                        
                        // 确保DOM更新后滚动到顶部
                        this.$nextTick(() => {
                            const contentWrapper = document.querySelector('.news-detail-content-wrapper');
                            if (contentWrapper) {
                                contentWrapper.scrollTop = 0;
                            }
                        });
                    } else {
                        this.$message.error('加载资讯详情失败');
                    }
                })
                .catch(error => {
                    console.error('加载资讯详情失败:', error);
                    this.$message.error('加载资讯详情失败');
                });
        },

        // 预览封面图片（现在不再需要单独弹窗）
        previewCoverImage(imageUrl) {
            if (!imageUrl) return;
            // 现在直接展示在资讯详情中，不需要单独弹窗
        },
        
        // 格式化时间（简版）
        formatTime(time) {
            if (!time) return '';
            try {
                // 尝试不同的时间格式
                let date;
                if (typeof time === 'string') {
                    // 移除可能的时间部分，只保留日期
                    const dateStr = time.split(' ')[0];
                    date = new Date(dateStr);
                } else {
                    date = new Date(time);
                }
                
                if (isNaN(date.getTime())) {
                    return time; // 如果解析失败，返回原始字符串
                }
                
                const month = (date.getMonth() + 1).toString().padStart(2, '0');
                const day = date.getDate().toString().padStart(2, '0');
                return `${month}-${day}`;
            } catch (e) {
                console.error('时间格式化错误:', e, time);
                return time;
            }
        },
        
        // 格式化时间（详细版）
        formatDetailTime(time) {
            if (!time) return '';
            try {
                let date;
                if (typeof time === 'string') {
                    date = new Date(time);
                } else {
                    date = new Date(time);
                }
                
                if (isNaN(date.getTime())) {
                    return time;
                }
                
                 return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
            } catch (e) {
                return time;
            }
        },
        
        // 跳转到资讯页面
        goToNewsPage() {
            this.$message.info('跳转到资讯页面功能待开发');
        },
        
        // 点击注册确认按钮
        register(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    request
                        .get("patient/addPatient", {
                            params: {
                                pId: this.registerForm.pId,
                                pName: this.registerForm.pName,
                                pPassword: this.registerForm.pPassword,
                                pGender: this.registerForm.pGender,
                                pEmail: this.registerForm.pEmail,
                                pPhone: this.registerForm.pPhone,
                                pCard: this.registerForm.pCard,
                                pBirthday: this.registerForm.pBirthday,
                            },
                        })
                        .then((res) => {
                            if (res.data.status !== 200)
                                return this.$message.error(
                                    "账号或邮箱已被占用！"
                                );
                            this.registerFormVisible = false;
                            this.$message.success("注册成功！");
                        });
                } else {
                    return false;
                }
            });
        },
        
        // 提交表单
        submitLoginForm(formName) {
            if (!/^\d+$/.test(this.loginForm.id)) {
                this.$message.error("用户名有误");
                return;
            }
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    if (this.role === "管理员") {
                        var params = new URLSearchParams();
                        params.append("aId", this.loginForm.id);
                        params.append("aPassword", this.loginForm.password);

                        request
                            .post("admin/login", params)
                            .then((res) => {
                                if (res.data.status != 200)
                                    return this.$message.error(
                                        "用户名或密码错误"
                                    );
                                setToken(res.data.data.token);
                                this.$router.push("/admin/adminLayout");
                            })
                            .catch((error) => {
                                this.$message.error(
                                    "服务器异常：" + error.response.data
                                );
                            });
                    }
                    if (this.role === "医生") {
                        var params1 = new URLSearchParams();
                        params1.append("dId", this.loginForm.id);
                        params1.append("dPassword", this.loginForm.password);

                        request
                            .post("doctor/login", params1)
                            .then((res) => {
                                if (res.data.status != 200)
                                    return this.$message.error(
                                        "用户名或密码错误"
                                    );
                                setToken(res.data.data.token);
                                this.$router.push("/doctor/doctorLayout");
                            })
                            .catch((error) => {
                                this.$message.error(
                                    "服务器异常：" + error.response.data
                                );
                            });
                    }
                    if (this.role === "患者") {
                        var params2 = new URLSearchParams();
                        params2.append("pId", this.loginForm.id);
                        params2.append("pPassword", this.loginForm.password);

                        request
                            .post("patient/login", params2)
                            .then((res) => {
                                if (res.data.status != 200)
                                    return this.$message.error(
                                        "用户名或密码错误"
                                    );
                                setToken(res.data.data.token);
                                this.$router.push("/patient/patientLayout");
                            })
                            .catch((error) => {
                                this.$message.error(
                                    "服务器异常：" + error.response.data
                                );
                            });
                    }
                } else {
                    return false;
                }
            });
        },
    },
};
</script>

<style lang="scss">
.codeInput {
    width: 70%;
    margin-right: 10px;
}

.login-index {
    display: flex;
    height: 100vh;
    justify-content: center;
    align-items: center;
    position: relative;
    overflow: hidden;
}

/* ==================== 侧边栏切换按钮 ==================== */
.sidebar-toggle {
    position: fixed;
    left: 20px;
    top: 20px;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    z-index: 1000;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    backdrop-filter: blur(10px);
    
    &:hover {
        background: rgba(255, 255, 255, 1);
        transform: scale(1.1);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
    }
    
    i {
        font-size: 20px;
        color: #e75c09;
        transition: transform 0.3s;
    }
    
    &:hover i {
        transform: rotate(180deg);
    }
}

/* ==================== 资讯侧边栏样式 ==================== */
.news-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    width: 380px;
    z-index: 100;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    
    /* 收起状态 */
    &.collapsed {
        width: 80px;
        
        .news-sidebar-container {
            width: 80px;
            padding: 20px 15px;
            
            .news-header {
                padding-bottom: 15px;
                margin-bottom: 20px;
                border-bottom: 1px solid rgba(231, 92, 9, 0.3);
                
                i {
                    font-size: 24px;
                }
            }
            
            .news-content.collapsed {
                .collapsed-view {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    height: 100%;
                    
                    .collapsed-count {
                        position: relative;
                        margin-bottom: 30px;
                        cursor: pointer;
                        
                        i {
                            font-size: 24px;
                            color: #e75c09;
                        }
                        
                        .count-badge {
                            position: absolute;
                            top: -8px;
                            right: -8px;
                            background: #ff5722;
                            color: white;
                            font-size: 12px;
                            font-weight: bold;
                            width: 20px;
                            height: 20px;
                            border-radius: 50%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            box-shadow: 0 2px 4px rgba(255, 87, 34, 0.3);
                        }
                    }
                    
                    .collapsed-preview {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        cursor: pointer;
                        padding: 15px;
                        border-radius: 8px;
                        background: rgba(231, 92, 9, 0.1);
                        transition: all 0.3s;
                        
                        &:hover {
                            background: rgba(231, 92, 9, 0.2);
                            transform: translateY(-2px);
                        }
                        
                        i {
                            font-size: 28px;
                            color: #e75c09;
                            margin-bottom: 8px;
                        }
                        
                        span {
                            font-size: 14px;
                            color: #e75c09;
                            font-weight: 500;
                        }
                    }
                }
            }
        }
    }
    
    .news-sidebar-container {
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.88);
        backdrop-filter: blur(15px);
        -webkit-backdrop-filter: blur(15px);
        box-shadow: 5px 0 25px rgba(0, 0, 0, 0.15);
        border-right: 1px solid rgba(255, 255, 255, 0.3);
        padding: 25px;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        
        &:hover {
            background: rgba(255, 255, 255, 0.92);
            box-shadow: 8px 0 30px rgba(0, 0, 0, 0.2);
            transform: translateX(2px);
        }
        
        .news-header {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 2px solid rgba(231, 92, 9, 0.7);
            transition: all 0.3s;
            
            i {
                color: #e75c09;
                font-size: 26px;
                transition: all 0.3s;
            }
            
            .news-title-text {
                color: #333;
                font-size: 22px;
                font-weight: bold;
                margin-left: 12px;
                letter-spacing: 1px;
                white-space: nowrap;
                transition: all 0.3s;
            }
        }
        
        .loading-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            color: #666;
            
            i {
                font-size: 32px;
                color: #e75c09;
                margin-bottom: 15px;
                animation: spin 1s linear infinite;
            }
            
            span {
                font-size: 16px;
            }
        }
        
        .empty-news {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            color: #999;
            
            i {
                font-size: 48px;
                margin-bottom: 15px;
            }
            
            p {
                font-size: 16px;
            }
        }
        
        .news-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            
            .collapsed-view {
                display: none;
            }
            
            .expanded-content {
                flex: 1;
                display: flex;
                flex-direction: column;
                overflow: hidden;
            }
        }
        
        /* 可滚动容器样式 */
        .scrollable-container {
            overflow-y: auto;
            overflow-x: hidden;
            padding-right: 10px;
            
            /* Webkit浏览器滚动条样式 */
            &::-webkit-scrollbar {
                width: 8px;
                background-color: transparent;
            }
            
            &::-webkit-scrollbar-track {
                background-color: rgba(0, 0, 0, 0.05);
                border-radius: 4px;
                margin: 4px 0;
            }
            
            &::-webkit-scrollbar-thumb {
                background-color: rgba(0, 0, 0, 0.2);
                border-radius: 4px;
                transition: all 0.3s ease;
                
                &:hover {
                    background-color: rgba(0, 0, 0, 0.3);
                }
                
                &:active {
                    background-color: rgba(0, 0, 0, 0.4);
                }
            }
            
            /* Firefox滚动条样式 */
            scrollbar-width: thin;
            scrollbar-color: rgba(0, 0, 0, 0.2) rgba(0, 0, 0, 0.05);
        }
        
        .top-news-section {
            margin-bottom: 25px;
            flex-shrink: 0;
            
            .section-title {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                font-size: 17px;
                font-weight: bold;
                color: #333;
                padding-right: 10px;
                
                i {
                    color: #ff9800;
                    margin-right: 10px;
                    font-size: 18px;
                }
                
                span {
                    color: #333;
                }
            }
            
            .top-news-list {
                max-height: 280px;
                min-height: 100px;
                
                .top-news-item {
                    position: relative;
                    background: rgba(255, 248, 225, 0.9);
                    border-left: 4px solid #ff9800;
                    padding: 15px;
                    margin-bottom: 12px;
                    border-radius: 6px;
                    cursor: pointer;
                    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                    backdrop-filter: blur(5px);
                    margin-right: 2px;
                    
                    &:hover {
                        transform: translateX(5px) translateY(-2px);
                        box-shadow: 0 6px 15px rgba(255, 152, 0, 0.25);
                        background: rgba(255, 248, 225, 1);
                    }
                    
                    .news-cover {
                        width: 100%;
                        height: 120px;
                        margin-bottom: 10px;
                        border-radius: 4px;
                        overflow: hidden;
                        position: relative;
                        
                        img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                            transition: transform 0.3s;
                            cursor: pointer;
                            
                            &:hover {
                                transform: scale(1.05);
                            }
                        }
                    }
                    
                    .top-tag {
                        position: absolute;
                        right: 12px;
                        top: 12px;
                        background: linear-gradient(135deg, #ff9800, #ff5722);
                        color: white;
                        padding: 4px 10px;
                        border-radius: 12px;
                        font-size: 12px;
                        font-weight: bold;
                        box-shadow: 0 2px 5px rgba(255, 87, 34, 0.3);
                        z-index: 2;
                    }
                    
                    .news-title {
                        font-size: 15px;
                        font-weight: 600;
                        color: #333;
                        margin-bottom: 10px;
                        line-height: 1.4;
                        padding-right: 60px;
                    }
                    
                    .news-info {
                        display: flex;
                        justify-content: space-between;
                        font-size: 12px;
                        color: #666;
                        
                        .news-type {
                            background: rgba(255, 152, 0, 0.15);
                            padding: 4px 8px;
                            border-radius: 4px;
                            font-weight: 500;
                        }
                        
                        .news-time {
                            opacity: 0.8;
                        }
                    }
                }
            }
        }
        
        .latest-news-section {
            flex: 1;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            
            .section-title {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                font-size: 17px;
                font-weight: bold;
                color: #333;
                padding-right: 10px;
                flex-shrink: 0;
                
                i {
                    color: #409eff;
                    margin-right: 10px;
                    font-size: 18px;
                }
                
                span {
                    color: #333;
                }
            }
            
            .latest-news-list {
                flex: 1;
                min-height: 200px;
                
                .latest-news-item {
                    display: flex;
                    padding: 14px;
                    margin-bottom: 10px;
                    background: rgba(255, 255, 255, 0.9);
                    border-radius: 6px;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
                    cursor: pointer;
                    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    backdrop-filter: blur(5px);
                    margin-right: 2px;
                    
                    &:hover {
                        transform: translateX(5px);
                        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
                        background: rgba(255, 255, 255, 1);
                        border-color: rgba(64, 158, 255, 0.3);
                    }
                    
                    .news-cover {
                        width: 80px;
                        height: 60px;
                        margin-right: 12px;
                        border-radius: 4px;
                        overflow: hidden;
                        flex-shrink: 0;
                        
                        img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                            transition: transform 0.3s;
                            cursor: pointer;
                            
                            &:hover {
                                transform: scale(1.05);
                            }
                        }
                    }
                    
                    .item-content {
                        flex: 1;
                        min-width: 0; /* 防止内容溢出 */
                        
                        .news-title {
                            font-size: 14px;
                            font-weight: 500;
                            color: #333;
                            margin-bottom: 8px;
                            line-height: 1.4;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                        }
                        
                        .news-meta {
                            display: flex;
                            justify-content: space-between;
                            font-size: 12px;
                            color: #666;
                            
                            .news-type {
                                background: rgba(64, 158, 255, 0.1);
                                padding: 4px 8px;
                                border-radius: 4px;
                                color: #409eff;
                                font-weight: 500;
                                white-space: nowrap;
                            }
                            
                            .news-time {
                                opacity: 0.8;
                                white-space: nowrap;
                            }
                            
                            .news-views {
                                opacity: 0.8;
                                white-space: nowrap;
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ==================== 登录窗口样式 ==================== */
.login-window-index {
    opacity: 0.9;
    width: 450px;
    height: 390px;
    background: rgba(255, 255, 255, 0.92);
    position: absolute;
    left: calc(50% + 190px); 
    top: 50%;
    transform: translateY(-50%);
    border-radius: 12px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    transition: all 0.4s;
}

/* 登录表单样式 */
.logo-index {
    background: rgba(255, 255, 255, 0.9);
    height: 130px;
    width: 130px;
    border-radius: 50%;
    padding: 10px;
    position: absolute;
    left: 50%;
    top: 0;
    transform: translate(-50%, -50%);
    border: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
    backdrop-filter: blur(5px);

    img {
        height: 100%;
        width: 100%;
        border-radius: 50%;
        background: #eeeeee;
    }
}

.loginForm {
    margin-top: 120px;
}

.el-form-item {
    margin-left: 20px;
    margin-right: 20px;
}

.role_radio {
    margin-left: 20px;
    margin-right: 90px;
}

.login-buttons {
    display: flex;
    justify-content: flex-end;
    height: 25px;
}

/* ==================== 资讯详情弹窗样式（合并图片展示） ==================== */
.news-detail-dialog {
    width: 80% !important;
    max-width: 1200px !important;
    height: 85vh !important;
    top: 3vh !important;
    
    .el-dialog__body {
        padding: 0 !important;
        height: calc(100% - 60px) !important;
        overflow: hidden;
    }
}

.news-detail {
    display: flex;
    flex-direction: column;
    height: 100%;
    
    /* 封面图片全屏展示区域 */
    .news-cover-fullscreen {
        height: 45%;
        min-height: 300px;
        overflow: hidden;
        background: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        border-bottom: 1px solid #e4e7ed;
        
        .fullscreen-cover-image {
            max-width: 100%;
            max-height: 100%;
            width: auto;
            height: auto;
            object-fit: contain;
            border-radius: 0;
            box-shadow: 0 5px 25px rgba(0, 0, 0, 0.1);
        }
    }
    
    /* 主要内容区域 */
    .news-detail-main {
        flex: 1;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        padding: 20px;
        
        .news-detail-header {
            margin-bottom: 20px;
            flex-shrink: 0;
            
            .news-detail-title {
                color: #333;
                margin-bottom: 15px;
                font-size: 24px;
                font-weight: 600;
                line-height: 1.4;
                text-align: left;
            }
            
            .news-detail-meta {
                display: flex;
                justify-content: flex-start;
                gap: 30px;
                margin-bottom: 20px;
                padding: 12px 20px;
                background: rgba(245, 245, 245, 0.8);
                border-radius: 6px;
                color: #666;
                backdrop-filter: blur(5px);
                
                .meta-item {
                    display: flex;
                    align-items: center;
                    font-size: 14px;
                    
                    i {
                        margin-right: 6px;
                        font-size: 16px;
                    }
                    
                    &:nth-child(1) i {
                        color: #ff9800;
                    }
                    
                    &:nth-child(2) i {
                        color: #409eff;
                    }
                    
                    &:nth-child(3) i {
                        color: #67c23a;
                    }
                }
            }
        }
        
        .news-detail-content-wrapper {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
            border-radius: 6px;
            background: rgba(250, 250, 250, 0.8);
            border: 1px solid rgba(0, 0, 0, 0.08);
            
            /* 内容区域滚动条 */
            &::-webkit-scrollbar {
                width: 8px;
            }
            
            &::-webkit-scrollbar-track {
                background-color: rgba(0, 0, 0, 0.05);
                border-radius: 4px;
            }
            
            &::-webkit-scrollbar-thumb {
                background-color: rgba(0, 0, 0, 0.15);
                border-radius: 4px;
                
                &:hover {
                    background-color: rgba(0, 0, 0, 0.25);
                }
            }
        }
        
        .news-detail-content {
            line-height: 1.8;
            font-size: 16px;
            color: #333;
            
            p {
                margin-bottom: 16px;
                text-align: justify;
            }
            
            img {
                max-width: 100%;
                height: auto;
                display: block;
                margin: 15px auto;
                border-radius: 6px;
                box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
            }
            
            h1, h2, h3, h4 {
                margin: 20px 0 15px 0;
                color: #333;
                font-weight: 600;
            }
            
            ul, ol {
                margin: 15px 0;
                padding-left: 30px;
                
                li {
                    margin-bottom: 8px;
                }
            }
            
            a {
                color: #409eff;
                text-decoration: none;
                
                &:hover {
                    text-decoration: underline;
                }
            }
            
            blockquote {
                border-left: 4px solid #409eff;
                margin: 20px 0;
                padding: 10px 20px;
                background: rgba(64, 158, 255, 0.05);
                border-radius: 0 6px 6px 0;
                font-style: italic;
            }
        }
    }
}

/* ==================== 响应式调整 ==================== */
@media (max-width: 1200px) {
    .news-sidebar {
        width: 350px;
        
        &.collapsed {
            width: 70px;
            
            .news-sidebar-container {
                width: 70px;
                padding: 15px 10px;
            }
        }
    }
    
    .login-window-index {
        left: calc(50% + 175px);
    }
    
    .news-sidebar.collapsed ~ .login-window-index {
        left: calc(50% + 35px);
    }
}

@media (max-width: 992px) {
    .login-index {
        flex-direction: column;
        padding: 20px;
        justify-content: flex-start;
        padding-top: 40px;
    }
    
    .sidebar-toggle {
        position: absolute;
        left: 20px;
        top: 20px;
        z-index: 1001;
    }
    
    .news-sidebar {
        position: relative;
        width: 100%;
        max-width: 500px;
        height: 500px;
        margin-bottom: 30px;
        backdrop-filter: none;
        margin-top: 20px;
        
        &.collapsed {
            width: 100px;
            max-width: 100px;
            height: 120px;
            
            .news-sidebar-container {
                border-radius: 12px;
                border-right: none;
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
                padding: 15px;
                
                &:hover {
                    transform: translateY(-2px);
                }
            }
        }
        
        .news-sidebar-container {
            border-radius: 12px;
            border-right: none;
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
            
            &:hover {
                transform: translateY(-2px);
            }
        }
    }
    
    .login-window-index {
        position: relative;
        left: 0;
        top: 0;
        transform: none;
        width: 100%;
        max-width: 450px;
        margin-bottom: 40px;
    }
    
    .news-sidebar.collapsed ~ .login-window-index {
        left: 0;
    }
    
    .news-detail-dialog {
        width: 95% !important;
        height: 90vh !important;
        
        .news-cover-fullscreen {
            height: 35%;
            min-height: 250px;
        }
    }
}

@media (max-width: 768px) {
    .news-sidebar {
        height: 450px;
        
        &.collapsed {
            height: 100px;
        }
        
        .news-sidebar-container {
            padding: 20px;
            
            .top-news-list {
                max-height: 220px;
            }
        }
    }
    
    .login-window-index {
        height: 370px;
    }
    
    .latest-news-item {
        flex-direction: column;
        
        .news-cover {
            width: 100% !important;
            height: 100px !important;
            margin-right: 0 !important;
            margin-bottom: 10px;
        }
    }
    
    .news-detail-dialog {
        height: 95vh !important;
        top: 2vh !important;
        
        .news-cover-fullscreen {
            height: 30%;
            min-height: 200px;
        }
        
        .news-detail-title {
            font-size: 20px !important;
        }
        
        .news-detail-meta {
            flex-direction: column;
            gap: 10px !important;
            align-items: flex-start;
            padding: 10px !important;
        }
    }
}

/* ==================== 动画效果 ==================== */
@keyframes slideIn {
    from {
        transform: translateX(-100%);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.news-sidebar {
    animation: slideIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.collapsed-view {
    animation: fadeIn 0.3s ease-out;
}

/* 原有的其他样式 */
.view-more {
    text-align: center;
    padding: 16px;
    color: #409eff;
    cursor: pointer;
    font-weight: 500;
    border-top: 1px solid rgba(0, 0, 0, 0.08);
    margin-top: 20px;
    transition: all 0.3s;
    
    &:hover {
        color: #337ecc;
        background: rgba(64, 158, 255, 0.05);
    }
}
</style>