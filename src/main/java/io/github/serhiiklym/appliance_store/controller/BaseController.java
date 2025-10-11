package io.github.serhiiklym.appliance_store.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class BaseController {

    private static final String FLASH_SUCCESS     = "flashSuccess";     // string or i18n code
    private static final String FLASH_ERROR       = "flashError";       // plain string fallback
    private static final String FLASH_ERROR_CODE  = "flashErrorCode";   // i18n code
    private static final String FLASH_ERROR_ARGS  = "flashErrorArgs";   // Object[] for code args

    protected void flashSuccess(RedirectAttributes ra, String messageOrCode) {
        ra.addFlashAttribute(FLASH_SUCCESS, messageOrCode);
    }

    protected void flashErrorText(RedirectAttributes ra, String plainText) {
        ra.addFlashAttribute(FLASH_ERROR, plainText);
    }

    protected void flashErrorCode(RedirectAttributes ra, String code, Object... args) {
        ra.addFlashAttribute(FLASH_ERROR_CODE, code);
        if (args != null && args.length > 0) {
            ra.addFlashAttribute(FLASH_ERROR_ARGS, args);
        }
    }
}
