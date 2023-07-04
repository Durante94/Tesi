// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
import { keycloackLogin } from '../e2e/generalFunction';

const resizeObserverLoopErrRe = /^[^(ResizeObserver loop limit exceeded)]/
Cypress.on('uncaught:exception', (err) => {
  /* returning false here prevents Cypress from failing the test */
  if (resizeObserverLoopErrRe.test(err.message)) {
    return false
  }
})
Cypress.on(
  'uncaught:exception',
  (err) => !err.message.includes('ResizeObserver loop limit exceeded')
);
Cypress.on('uncaught:exception', (err, runnable) => {
  // returning false here prevents Cypress from
  // failing the test
  return false
})

Cypress.Commands.add('loginToKeycloack', (username, password, centrale = false) => {
  const log = Cypress.log({
    displayName: 'KEYCLOACK LOGIN',
    message: [`🔐 Authenticating | ${username}`],
    autoEnd: false
  });
  log.snapshot('before');
  cy.session(
    'logged-as-' + username,
    () => keycloackLogin(username, password, centrale),
    {
      validate: () => {
        cy.wait(1000);
        cy.request(`${Cypress.env('base_url' + (centrale ? '_cent' : ''))}/gateway/auth/cypress`).its('body').should('equal', true);
      },
      cacheAcrossSpecs: true
    }
  );
  log.snapshot('after');
  log.end();
})