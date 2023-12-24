export class HeaderPo {

  private static headerSelector = '[data-test="header"]';

    static home() {
        return cy.get(`${this.headerSelector} a[href="/latest"]`);
    }

    static portofolio() {
        return cy.get(`${this.headerSelector} a[href="/portofolio"]`);
    }

    static admin() {
        return cy.get(`${this.headerSelector} a[href="/admin"]`);
    }

    static login() {
        return cy.get(`${this.headerSelector} [data-test="login"]`);
    }

    static signup() {
        return cy.get(`${this.headerSelector} [data-test="signup"]`);
    }

    static currencyQuantity() {
        return cy.get(`${this.headerSelector} [data-test="currency-quantity"]`);
    }
}
