/*
 * blanco Framework
 * Copyright (C) 2004-2020 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.veevalidate;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import blanco.valueobjectts.task.BlancoValueObjectTsProcessImpl;
import blanco.valueobjectts.task.valueobject.BlancoValueObjectTsProcessInput;
import blanco.veevalidate.task.BlancoVeeValidateProcessImpl;
import blanco.veevalidate.task.valueobject.BlancoVeeValidateProcessInput;

/**
 * Generation test for TypeScript.
 *
 * @author IGA Tosiki
 * @author tueda
 */
public class BlancoVeeValidateTest {

    @Test
    public void testBlancoVeeValidate() {
        /* First, creates a ValueObject. */
        BlancoValueObjectTsProcessInput input = new BlancoValueObjectTsProcessInput();
        input.setMetadir("meta/objects");
        input.setEncoding("UTF-8");
        input.setSheetType("php");
        input.setTmpdir("tmpObjects");
        input.setTargetdir("sample/blanco");
        input.setTargetStyle("maven");
        input.setTabs(2);
        input.setVerbose(true);
        input.setLineSeparator("LF");

        BlancoValueObjectTsProcessImpl imple = new BlancoValueObjectTsProcessImpl();
        try {
            imple.execute(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Then, creates a VueComponent. */
        BlancoVeeValidateProcessInput inputVeeValidate = new BlancoVeeValidateProcessInput();
        inputVeeValidate.setMetadir("meta/validators");
        inputVeeValidate.setEncoding("UTF-8");
        inputVeeValidate.setSheetType("php");
        inputVeeValidate.setTmpdir("tmpTest");
        inputVeeValidate.setSearchTmpdir("tmpObjects");
        inputVeeValidate.setTargetdir("sample/blanco/main/validators");
        inputVeeValidate.setTargetStyle("free");
        inputVeeValidate.setTabs(2);
        inputVeeValidate.setVerbose(true);

        BlancoVeeValidateProcessImpl imple2 = new BlancoVeeValidateProcessImpl();
        try {
            imple2.execute(inputVeeValidate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
