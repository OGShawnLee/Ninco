package ninco.gui.controller;

public class GUILandingAdminPageController extends LandingController {
  public void onClickReviewEmployeeList() {
    GUIReviewEmployeeListPageController.navigateToEmployeeListPage(getScene());
  }

  public void onClickReviewProductList() {
    GUIReviewProductListPageController.navigateToProductListPage(getScene());
  }

  public void onClickReviewStoreList() {
    GUIReviewStoreListPageController.navigateToStoreListPage(getScene());
  }

  public void onClickReviewStock() {
    GUIReviewStockListPageController.navigateToStockListPage(getScene());
  }
}