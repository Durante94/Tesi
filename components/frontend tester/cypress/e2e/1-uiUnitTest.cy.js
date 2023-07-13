import { openDropdown } from "./commonMenuFunctions";

describe("Device Lifecycle", () => {
    const devName = "cypressTest";
    const devDescr = "Creato con Cypress";

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
            url: '/api/agent*',
        }).as('apiAgent')
        cy.intercept({
            method: 'POST',
            url: '/api/crud',
        }).as('apiSave')

        cy.get(".cp-add-dev").should("exist").click();

        //CLICK SU SALVA TRIGGERA ERRORE CAMPI NON COMPILATI
        cy.get(".cp-save").click();
        cy.get(".cp-name").type(devName);
        cy.get(".cp-description").type(devDescr);
        cy.get(".cp-agent").click();
        cy.wait("@apiAgent", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
        cy.get(".ant-select-item").first().click();
        cy.get(".cp-enable").click();

        cy.get(".cp-amplitude input").should("be.disabled").invoke("val").should("be.empty");
        cy.get(".cp-frequency input").should("be.disabled").invoke("val").should("be.empty");
        cy.get(".cp-function input").should("be.disabled").invoke("val").should("be.empty");

        cy.get(".cp-save").should("not.be.disabled");

        cy.get(".cp-config-req").should("not.be.disabled").click();

        // cy.get(".cp-agent").click();
        // cy.get(".ant-select-item").eq(1).click();
        // cy.get(".cp-save").should("be.disabled");

        cy.get(".cp-agent").click();
        cy.get(".ant-select-item").first().click();
        cy.get(".cp-save").should("not.be.disabled");

        cy.get(".cp-amplitude input", { timeout: 5000 }).should("be.disabled").invoke("val").should("not.be.empty");
        cy.get(".cp-frequency input", { timeout: 5000 }).should("be.disabled").invoke("val").should("not.be.empty");
        cy.get(".cp-function input", { timeout: 5000 }).should("be.disabled").invoke("val").should("not.be.empty");

        cy.get(".cp-save").click();
        cy.wait("@apiSave", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    });

    it("Editing newly created device", () => {
        cy.intercept({
            method: 'GET',
            url: '/api/crud?*',
        }).as('apiList')
        cy.intercept({
            method: 'GET',
            url: '/api/crud/*',
        }).as('apiDetail')
        cy.intercept({
            method: 'POST',
            url: '/api/crud',
        }).as('apiSave')

        openDropdown(1);
        cy.get(".cp-inpNomeDev").type(`{selectAll}{backSpace}${devName}`);
        cy.get(".cp-cerca").click();
        cy.wait("@apiList", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });

        cy.get(".cp-edit").should("not.be.disabled").click()
        cy.wait("@apiDetail", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });

        cy.get(".cp-amplitude input").should("be.disabled").invoke("val").should("not.be.empty");
        cy.get(".cp-frequency input").should("be.disabled").invoke("val").should("not.be.empty");
        cy.get(".cp-function input").should("be.disabled").invoke("val").should("not.be.empty");
        cy.get(".cp-description").type(" Edit");

        cy.get(".cp-save").should("not.be.disabled").click();
        cy.wait("@apiSave", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    });

    it("Restore description and change agent", () => {
        cy.intercept({
            method: 'GET',
            url: '/api/crud/*',
        }).as('apiDetail')
        cy.intercept({
            method: 'POST',
            url: '/api/crud',
        }).as('apiSave')
        cy.intercept({
            method: 'GET',
            url: '/api/agent*',
        }).as('apiAgent')

        cy.get(".cp-edit").should("not.be.disabled").click()
        cy.wait("@apiDetail", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });

        cy.get(".cp-amplitude input").should("be.disabled").invoke("val").should("not.be.empty").then(val => cy.wrap(val).as("amplitude"));
        cy.get(".cp-frequency input").should("be.disabled").invoke("val").should("not.be.empty").then(val => cy.wrap(val).as("frequency"));
        cy.get(".cp-function input").should("be.disabled").invoke("val").should("not.be.empty").then(val => cy.wrap(val).as("function"));
        cy.get(".cp-description input").invoke("val").then(val => expect(val).to.be.equals(devDescr + " Edit"));
        cy.get(".cp-description").type(`{selectAll}{backSpace}${devDescr}`);

        cy.get(".cp-agent").click();
        cy.wait("@apiAgent", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
        cy.get(".ant-select-item").eq(2).click();
        cy.get(".cp-save").should("be.disabled");

        cy.get(".cp-config-req").should("not.be.disabled").click();

        cy.wait(5000);
        cy.get(".cp-amplitude input").should("be.disabled").invoke("val").should("not.be.empty")
            .then(val => cy.get("@amplitude").should("not.be.equals", val));
        cy.get(".cp-frequency input").should("be.disabled").invoke("val").should("not.be.empty")
            .then(val => cy.get("@frequency").should("not.be.equals", val));
        // cy.get(".cp-function input").should("be.disabled").invoke("val").should("not.be.empty")
        //     .then(val => cy.get("@function").should("not.be.equals", val));

        cy.get(".cp-save").should("not.be.disabled").click();
        cy.wait("@apiSave", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    });

    it("Apertura in modalità sola lettura", () => {
        cy.intercept({
            method: 'GET',
            url: '/api/crud?*',
        }).as('apiList')
        cy.intercept({
            method: 'GET',
            url: '/api/crud/*',
        }).as('apiDetail')

        cy.get(".cp-search").should("not.be.disabled").click()
        cy.wait("@apiDetail", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });

        cy.get(".cp-amplitude input").should("be.disabled").invoke("val").should("not.be.empty")
        cy.get(".cp-frequency input").should("be.disabled").invoke("val").should("not.be.empty")
        cy.get(".cp-function input").should("be.disabled").invoke("val").should("not.be.empty")
        cy.get(".cp-description input").should("be.disabled").invoke("val").should("not.be.empty")
        cy.get(".cp-agent input").should("not.be.visible");
        cy.get(".cp-name input").should("be.disabled").invoke("val").should("not.be.empty");
        cy.get(".cp-enable input").should("be.disabled");

        cy.get(".cp-config-req").should("be.disabled");
        cy.get(".cp-save").should("be.disabled");
        cy.get(".cp-close").should("not.be.disabled").click();

        cy.wait("@apiList", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    });

    it("Cancellazione nuovo device", () => {
        cy.intercept({
            method: 'DELETE',
            url: '/api/crud/*',
        }).as('apiDelete')
        cy.get(".cp-delete").should("not.be.disabled").click();
        cy.get(".ant-modal-body .ant-btn").should("be.visible").click();
        cy.wait("@apiDelete", 9000000000000000).should(xhr => { expect(xhr.response).to.have.property('statusCode', 200) });
    })
});