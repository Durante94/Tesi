const { defineConfig } = require("cypress");

module.exports = defineConfig({
  projectId: "9qb584",
  chromeWebSecurity: false,
  env: {
    login_url: "http://localhost:30080/web/",
    base_url: "http://localhost:30080",
    keycloack_host: "https://localhost:30090",
    username: "fabrizio",
    password: "fabrizio",
    doLogin: true
  },
  viewportWidth: 1920,
  viewportHeight: 1080,

  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
    // experimentalSessionAndOrigin: true, //PERMETTE LA NAVIGAZIONE A UNA PAGINA CON ORIGINE DIVERSA
    experimentalModifyObstructiveThirdPartyCode: true, // PERMETTE LA SCRITTURA SU PAGINA CON ORIGINE DIVERSA
    testIsolation: false //RETRCOMPATIBILITÀ, EVITIAMO CHE AD OGNI TEST CASE VENGA RESETTATO TUTTO (COOKIE, NAVIGAZIONE, ECC.)
  },
});

