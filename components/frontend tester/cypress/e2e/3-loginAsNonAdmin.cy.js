describe("Check all disable", () => {
    before(() => {
        if (Cypress.env('doLogin'))
            cy.loginToKeycloack(
                Cypress.env('usernameNoAdmin'),
                Cypress.env('passwordNoAdmin')
            );
        cy.intercept({
            method: 'GET',
            url: '/api/crud/enable',
        }).as('userAdmin')
        cy.visit(Cypress.env('login_url'));
        cy.wait('@userAdmin', 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    })

    it("Check all disable in table view", () => {
        cy.intercept({
            method: 'GET',
            url: '/api/crud/*',
        }).as('apiDetail')

        cy.get(".cp-add-dev").should("be.disabled");
        cy.get("th .ant-checkbox-input").should("not.exist")
        cy.get("tbody .ant-checkbox-input").should("be.disabled")
        cy.get(".cp-delete button").should("be.disabled");
        cy.get(".cp-edit button").should("be.disabled");
        cy.get(".cp-search").should("not.be.disabled").first().click();
        cy.wait("@apiDetail", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    });

    it("Check all disable in form view", () => {
        cy.get(".cp-name input").should("be.disabled");
        cy.get(".cp-description input").should("be.disabled");
        cy.get(".cp-agent input").should("be.disabled");
        cy.get(".cp-enable input").should("be.disabled");
        cy.get(".cp-function input").should("be.disabled");
        cy.get(".cp-save").should("be.disabled");
        cy.get(".cp-config-req").should("be.disabled");
        cy.get(".cp-close").should("not.be.disabled").click();
    });
});