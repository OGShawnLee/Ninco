package ninco.gui.controller;

public class GUILandingCashierPageController extends LandingController {
  public void onClickSearchProduct() {}

  public void onClickStartNewSale() {
    GUIReviewStockListPageController.navigateToStockListPage(getScene());
  }
}
