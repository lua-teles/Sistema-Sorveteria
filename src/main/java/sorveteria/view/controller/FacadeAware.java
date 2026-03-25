package sorveteria.view.controller;

import sorveteria.facade.SorveteriaFacade;

/* td controller que precisa da SorveteriaFacade deve implementá-la
 MainController verifica esta interface ao trocar de tela e injeta
 a facade automaticamente — sem necessidade de singletons ou variáveis estáticas.
 */

public interface FacadeAware {
    void setFacade(SorveteriaFacade facade);
}
