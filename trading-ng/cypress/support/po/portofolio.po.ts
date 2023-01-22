export class PortofolioPo {

  static page() {
    return cy.get('app-portofolio');
  }

  static summary() {
    return this.page().find('clr-stack-view');
  }
}
