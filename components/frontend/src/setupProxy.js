const { createProxyMiddleware } = require('http-proxy-middleware');

export default function (app) {
    app.use(createProxyMiddleware("/api", { target: "http://localhost:8000" }));
    // app.use(
    //     proxy("/ws-message", {
    //         target: "ws://localhost:8001"
    //     })
    // );
};