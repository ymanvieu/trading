export class OrderPo {

  static ModalPo = class {

    static confirmOrder() {
      return OrderPo.modal().find(`[data-test="order-action"]`);
    }

    private static quantitySelector() {
      return OrderPo.modal().find(`[data-test="quantity-selection"] `);
    }

    static selectedQuantity() {
      return this.quantitySelector().find(`#input`);
    }

    static increment() {
      return this.quantitySelector().find(`.p-inputnumber-button-up`);
    }

    static decrement() {
      return this.quantitySelector().find(`.p-inputnumber-button-down`);
    }

    static errorMessage() {
      return OrderPo.modal().find('[data-test="error-message"]');
    }

    static symbolSelector() {
      return OrderPo.modal().find('[data-test="symbol-selector"]');
    }

    static sellAll() {
      return OrderPo.modal().find('[data-test="sell-all"]');
    }
  };

  static modal() {
    return cy.get('[data-test="order-modal"]');
  }

  static buyNewAsset() {
    return cy.get(`[data-test="buy-new-asset"]`);
  }

  static buy() {
    return cy.get(`[data-test="buy-asset"]`);
  }

  static sell() {
    return cy.get(`[data-test="sell-asset"]`);
  }

}
