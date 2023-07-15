describe("Device Enabling from table view", () => {
    before(() => {
        if (Cypress.env('doLogin'))
            cy.loginToKeycloack(
                Cypress.env('username'),
                Cypress.env('password')
            );
        cy.intercept({
            method: 'GET',
            url: '/api/crud/enable',
        }).as('userAdmin')
        cy.visit(Cypress.env('login_url'));
        cy.wait('@userAdmin', 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    })

    it("Check on header", () => {
        cy.intercept({
            method: 'POST',
            url: '/api/crud/enable/*',
        }).as('apiCheck')
        cy.intercept({
            method: 'GET',
            url: '/api/crud?*',
        }).as('apiList')

        cy.get("th .ant-checkbox-input").should("exist").invoke("attr", "checked").then(val => {
            let prefix = '';
            cy.log(val)
            if (val === "checked") {
                prefix = "not."
                cy.get("th .ant-checkbox-input").uncheck();
            }
            else
                cy.get("th .ant-checkbox-input").check();
            cy.wait("@apiCheck", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
            cy.wait("@apiList", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });

            cy.get("thead .ant-checkbox-input").should(prefix + "be.checked");
            cy.get("tbody .ant-checkbox-input").should(prefix + "be.checked");
        });
    });
});