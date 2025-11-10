package ninco.gui.controller;

import ninco.gui.modal.ModalFacade;
import ninco.gui.modal.ModalFacadeConfiguration;

public class GUILandingAdminPageController extends LandingController {
  public void onClickReviewEmployeeList() {
    GUIReviewEmployeeListPageController.navigateToEmployeeListPage(getScene());
  }

  public void onClickRegisterEmployee() {

  }

  public void onClickReviewProductList() {
    GUIReviewProductListPageController.navigateToProductListPage(getScene());
  }

  public void onClickRegisterProduct() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration("Register Product", "GUIRegisterProductModal")
    );
  }

  public void onClickReviewStoreList() {
    GUIReviewStoreListPageController.navigateToStoreListPage(getScene());
  }

  public void onClickRegisterStore() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration("Register Store", "GUIRegisterStoreModal")
    );
  }
}