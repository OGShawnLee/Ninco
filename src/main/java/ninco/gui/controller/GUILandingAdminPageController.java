package ninco.gui.controller;

import ninco.gui.modal.ModalFacade;
import ninco.gui.modal.ModalFacadeConfiguration;

public class GUILandingAdminPageController extends LandingController {
  public void onClickReviewEmployeeList() {

  }

  public void onClickRegisterEmployee() {

  }

  public void onClickReviewProductList() {

  }

  public void onClickRegisterProduct() {

  }

  public void onClickReviewStoreList() {
  }

  public void onClickRegisterStore() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration("Register Store", "GUIRegisterStoreModal")
    );
  }
}