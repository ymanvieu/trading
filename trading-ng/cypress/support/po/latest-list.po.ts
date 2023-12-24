export class LatestListPo {

    static table() {
        return cy.get('[data-test="latest-list-table"]');
    }

    static lines() {
        return cy.get('[data-test="table-line"]');
    }

    static favorites() {
        return cy.get('[data-test="favorite"]');
    }
}
