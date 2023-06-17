const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://192.168.230.129:30080',
            changeOrigin: true,
            // logLevel: "debug"
        })
    );
    app.use(
        createProxyMiddleware("/ws-message", {
            target: "ws://192.168.230.129:30080"
        })
    );
};