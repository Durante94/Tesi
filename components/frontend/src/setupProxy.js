const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://localhost:8000',
            changeOrigin: true,
            // logLevel: "debug"
        })
    );
    app.use(
        createProxyMiddleware("/ws-message", {
            target: "ws://localhost:8001"
        })
    );
};