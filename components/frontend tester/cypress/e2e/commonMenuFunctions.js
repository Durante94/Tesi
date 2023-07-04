export const openDropdown = (childNum) => {
    cy.get(`:nth-child(${childNum}) > .ant-table-filter-column > .ant-dropdown-trigger`).click();
}

export const numberForPage = () => {
    cy.get('.ant-pagination-options > .ant-select > .ant-select-selector').click();
}

export const changePage = (childNum) => {
    cy.get(`.ant-pagination-item-${childNum} > a`).click();
    cy.get('.ant-pagination-next > .ant-pagination-item-link')
}

export const prev = () => {
    cy.get('.ant-pagination-prev > .ant-pagination-item-link').click();
}
export const next = () => {

    cy.get('.ant-pagination-next > .ant-pagination-item-link').click();
    cy.get('.ant-pagination-prev > .ant-pagination-item-link').click();
}
export const openDropdownlast = (childNum) => {
    cy.get(`:nth-child(${childNum}) > .ant-table-filter-column > .ant-dropdown-trigger`).last().click();
}