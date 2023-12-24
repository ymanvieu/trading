export class AdminPo {

    private static page = 'app-admin';

    static lines() {
        return cy.get(`${this.page} [data-test="table-line"]`);
    }
}
