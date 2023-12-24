export class PortofolioPo {

  private static page = 'app-portofolio';

  static summary() {
    return cy.get(`${this.page} [data-test="summary"]`);
  }

  static assets() {
    return cy.get(`${this.page} [data-test="asset"]`);
  }

  static searchBar() {
    return cy.get(`${this.page} [data-test="search-bar"]`);
  }
  static searchIcon() {
    return cy.get(`${this.page} [data-test="search-icon"]`);
  }

  static searchClear() {
    return cy.get(`${this.page} [data-test="search-clear"]`);
  }
}
