describe("Device Lifecycle", () => {
    const devName = "cypressTest";

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

    it("Creation", () => {
        cy.intercept({
            method: 'GET',
            url: '/api/agent',
        }).as('apiAgent')
        cy.intercept({
            method: 'POST',
            url: '/api/crud',
        }).as('apiSave')

        cy.get(".cp-add-dev").should("exist").click();

        //CLICK SU SALVA TRIGGERA ERRORE CAMPI NON COMPILATI
        cy.get(".cp-save").click();
        cy.get(".cp-name").type(devName);
        cy.get("cp-description").type("Creato con Cypress");
        cy.get(".cp-agent").click();
        cy.wait("@apiAgent", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
        cy.get("").first().click();
        cy.get(".cp-enable").click();

        cy.get(".cp-amplitude").should("be.disabled").invoke("val").should("be.null");
        cy.get(".cp-frequency").should("be.disabled").invoke("val").should("be.null");
        cy.get(".cp-function").should("be.disabled").invoke("val").should("be.null");

        cy.get(".cp-save").click();
        cy.wait("@apiSave", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    });

    it("Editing newly created device", () => {

    });
});