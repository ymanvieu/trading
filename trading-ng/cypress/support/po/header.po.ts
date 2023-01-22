export class HeaderPo {

    static header() {
        return cy.get('clr-header');
    }

    static loginLink() {
        return HeaderPo.header().get('a[href="/login"]');
    }
}
