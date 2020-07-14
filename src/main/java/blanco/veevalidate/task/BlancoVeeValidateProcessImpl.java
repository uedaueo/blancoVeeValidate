/*
 * blanco Framework
 * Copyright (C) 2004-2008 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.veevalidate.task;

import blanco.cg.BlancoCgSupportedLang;
import blanco.veevalidate.*;
import blanco.veevalidate.message.BlancoVeeValidateMessage;
import blanco.veevalidate.task.valueobject.BlancoVeeValidateProcessInput;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlancoVeeValidateProcessImpl implements BlancoVeeValidateProcess {

    /**
     * メッセージ。
     */
    private final BlancoVeeValidateMessage fMsg = new BlancoVeeValidateMessage();

    /**
     * {@inheritDoc}
     */
    public int execute(final BlancoVeeValidateProcessInput input)
            throws IOException, IllegalArgumentException {
        System.out.println("- " + BlancoVeeValidateConstants.PRODUCT_NAME
                + " (" + BlancoVeeValidateConstants.VERSION + ")");
        try {
            final File fileMetadir = new File(input.getMetadir());
            if (fileMetadir.exists() == false) {
                throw new IllegalArgumentException(fMsg.getMbvoja01(input
                        .getMetadir()));
            }

            /*
             * 改行コードを決定します。
             */
            String LF = "\n";
            String CR = "\r";
            String CRLF = CR + LF;
            String lineSeparatorMark = input.getLineSeparator();
            String lineSeparator = "";
            if ("LF".equals(lineSeparatorMark)) {
                lineSeparator = LF;
            } else if ("CR".equals(lineSeparatorMark)) {
                lineSeparator = CR;
            } else if ("CRLF".equals(lineSeparatorMark)) {
                lineSeparator = CRLF;
            }
            if (lineSeparator.length() != 0) {
                System.setProperty("line.separator", lineSeparator);
                if (input.getVerbose()) {
                    System.out.println("lineSeparator try to change to " + lineSeparatorMark);
                    String newProp = System.getProperty("line.separator");
                    String newMark = "other";
                    if (LF.equals(newProp)) {
                        newMark = "LF";
                    } else if (CR.equals(newProp)) {
                        newMark = "CR";
                    } else if (CRLF.equals(newProp)) {
                        newMark = "CRLF";
                    }
                    System.out.println("New System Props = " + newMark);
                }
            }

            /*
             * targetdir, targetStyleの処理。
             * 生成されたコードの保管場所を設定する。
             * targetstyle = blanco:
             *  targetdirの下に main ディレクトリを作成
             * targetstyle = maven:
             *  targetdirの下に main/java ディレクトリを作成
             * targetstyle = free:
             *  targetdirをそのまま使用してディレクトリを作成。
             *  ただしtargetdirがからの場合はデフォルト文字列(blanco)使用する。
             * by tueda, 2019/08/30
             */
            String strTarget = input.getTargetdir();
            String style = input.getTargetStyle();
            // ここを通ったら常にtrue
            boolean isTargetStyleAdvanced = true;
            if (style != null && BlancoVeeValidateConstants.TARGET_STYLE_MAVEN.equals(style)) {
                strTarget = strTarget + "/" + BlancoVeeValidateConstants.TARGET_DIR_SUFFIX_MAVEN;
            } else if (style == null ||
                    !BlancoVeeValidateConstants.TARGET_STYLE_FREE.equals(style)) {
                strTarget = strTarget + "/" + BlancoVeeValidateConstants.TARGET_DIR_SUFFIX_BLANCO;
            }
            /* style が free だったらtargetdirをそのまま使う */
            if (input.getVerbose()) {
                System.out.println("/* tueda */ TARGETDIR = " + strTarget);
            }

            // テンポラリディレクトリを作成。
            new File(input.getTmpdir()
                    + BlancoVeeValidateConstants.TARGET_SUBDIRECTORY).mkdirs();

            new BlancoVeeValidateMeta2Xml().processDirectory(fileMetadir, input
                    .getTmpdir()
                    + BlancoVeeValidateConstants.TARGET_SUBDIRECTORY);

            // XML化されたメタファイルからValueObjectを生成
            // 最初にテンポラリフォルダを走査
            final File[] fileMeta2 = new File(input.getTmpdir()
                    + BlancoVeeValidateConstants.TARGET_SUBDIRECTORY)
                    .listFiles();

        /*
         * まず始めにすべてのシートを検索して，クラス名とpackage名のリストを作ります．
         * php形式の定義書では，クラスを指定する際にpackage名が指定されていないからです．
         */
            BlancoVeeValidateUtil.isVerbose = input.getVerbose();
            BlancoVeeValidateUtil.processValueObjects(input);

            List<BlancoVeeValidateClassStructure> validatorStructures = new ArrayList<>();

            // 次にメタディレクトリとして指定されているディレクトリを走査
            for (int index = 0; index < fileMeta2.length; index++) {
                if (fileMeta2[index].getName().endsWith(".xml") == false) {
                    continue;
                }

                BlancoVeeValidateXmlParser parser = new BlancoVeeValidateXmlParser();
                parser.setVerbose(input.getVerbose());
                final BlancoVeeValidateClassStructure[] structures = parser.parse(fileMeta2[index]);

                /*
                 * いったん、全部集めます
                 */
                for (int index2 = 0; index2 < structures.length; index2++) {
                    BlancoVeeValidateClassStructure classStructure = structures[index2];
                    validatorStructures.add(classStructure);
                }
            }

            /*
             * そして、ValidateInitializer クラスを生成します。
             */
            final BlancoVeeValidateXml2TypeScriptClass xml2Class = new BlancoVeeValidateXml2TypeScriptClass();
            xml2Class.setEncoding(input.getEncoding());
            xml2Class.setVerbose(input.getVerbose());
            xml2Class.setTargetStyleAdvanced(isTargetStyleAdvanced);
            xml2Class.setXmlRootElement(input.getXmlrootelement());
            xml2Class.setSheetLang(new BlancoCgSupportedLang().convertToInt(input.getSheetType()));
            xml2Class.setTabs(input.getTabs());
            xml2Class.process(validatorStructures, new File(strTarget));

            return BlancoVeeValidateBatchProcess.END_SUCCESS;
        } catch (TransformerException e) {
            throw new IOException("XML変換の過程で例外が発生しました: " + e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}
