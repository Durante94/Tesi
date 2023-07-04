import { openDropdown, openDropdownlast } from "./commonMenuFunctions";

export const functionFiltri = (num, numlist, numCerc, stringa) => {
    openDropdown(num);
    cy.wait(100)
    cy.get(".ant-input.cp-inpNomeDev").eq(numlist).type(stringa);
    cy.wait(100)
    cy.get(".cp-cerca").eq(numCerc).click();
}

export const functionFiltriNum = (num, numero, numlist, number) => {
    openDropdown(num);
    cy.wait(100)
    cy.get('.ant-input-number-input').eq(numero).type(number, { multiple: true });
    cy.wait(100)
    cy.get(".cp-cerca").eq(numlist).click();
}

export const functionFiltriSelect = (num, numero, numlist, stringa) => {
    openDropdown(num);
    cy.wait(100)
    cy.get('.ant-select-selection-item').eq(numero).click();
    cy.get('.ant-select-item.ant-select-item-option').eq(stringa).click();
    cy.wait(100)
    cy.get(".cp-cerca").eq(numlist).click();
}

export const functionFiltriReset = (num, numlist) => {
    openDropdown(num);
    //cy.get(".ant-input.cp-inpNomeDev").eq(numlist).type(stringa, { multiple: true });
    cy.get(".cp-reset").eq(numlist).click();
}

export const waitFor200 = (routeAlias, retries = 2) => {
    cy.wait(routeAlias).then(xhr => {
        if (xhr.status === 200) return // OK
        else if (retries > 0) waitFor200(routeAlias, retries - 1); // wait for the next response
        else throw "All requests returned non-200 response";
    })
}

export const waitOrPass = (time) => {
    if (open) {
        // time = 1000;
        cy.wait(time);
    } else cy.wait(0);
}

export const functionFiltrilast = (num, numlist, numCerc, stringa) => {
    openDropdownlast(num);
    cy.wait(100)
    cy.get(".ant-input.cp-inpNomeDev").eq(numlist).type(stringa, { multiple: true });
    cy.wait(100)
    cy.get(".cp-cerca").eq(numCerc).click();
}

export const objTextContainsWhat = (targetObj, text) => expect(targetObj.text().toLowerCase(), `Testo '${text}' non trovato`).to.equal(text)

export const regexCheck = (targetObj) => expect(targetObj.text(), `Regex non trovata`).to.equal("Caratteri consentiti: a..z A..Z 0..9 -_.");
export const regexCheckC3 = (targetObj) => expect(targetObj.text(), `Regex non trovata`).to.equal("Caratteri consentiti: a..z A..Z 0..9 -_.#");

export function keycloackLogin(username, password, centrale = false) {
    cy.log("keycloackLogin started")
    cy.origin(
        Cypress.env('keycloack_host'),
        { args: { username, password, centrale } },
        ({ username, password, centrale }) => {
            cy.log(`keycloackLogin compile fields: ${username} ${password}`)
            cy.visit(Cypress.env('login_url' + (centrale ? "_centrale" : '')));
            cy.get('#username').type(username)
            cy.get('#password').type(password, { log: false })
            cy.get('#kc-login').click();
        }
    );
}