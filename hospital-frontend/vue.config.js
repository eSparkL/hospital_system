/*
 * vue配置
 */
module.exports = {
    lintOnSave: false,
    devServer: {
        host: "localhost",
        port: 9282,
        https: false,
        proxy: "http://localhost:9281",
        overlay: {
            warning: false,
            errors: false
        },
    }
}
