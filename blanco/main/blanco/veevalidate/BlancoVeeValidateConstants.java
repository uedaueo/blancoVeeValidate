package blanco.veevalidate;

/**
 * blancoVeeValidateが利用する定数クラス。
 */
public class BlancoVeeValidateConstants {
    /**
     * 項目番号:1<br>
     * プロダクト名。英字で指定します。
     */
    public static final String PRODUCT_NAME = "blancoVeeValidate";

    /**
     * 項目番号:2<br>
     * プロダクト名の小文字版。英字で指定します。
     */
    public static final String PRODUCT_NAME_LOWER = "blancoveevalidate";

    /**
     * 項目番号:3<br>
     * バージョン番号。
     */
    public static final String VERSION = "3.0.6";

    /**
     * 項目番号:4<br>
     * 処理の過程で利用されるサブディレクトリ。
     */
    public static final String TARGET_SUBDIRECTORY = "/veevalidate";

    /**
     * 項目番号:5<br>
     * targetdirに設定される文字列
     */
    public static final String TARGET_STYLE_BLANCO = "blanco";

    /**
     * 項目番号:6<br>
     * targetdirに設定される文字列
     */
    public static final String TARGET_STYLE_MAVEN = "maven";

    /**
     * 項目番号:7<br>
     * targetdirに設定される文字列
     */
    public static final String TARGET_STYLE_FREE = "free";

    /**
     * 項目番号:8<br>
     * 生成したソースコードを保管するディレクトリのsuffix
     */
    public static final String TARGET_DIR_SUFFIX_BLANCO = "main";

    /**
     * 項目番号:9<br>
     * 生成したソースコードを保管するディレクトリのsuffix
     */
    public static final String TARGET_DIR_SUFFIX_MAVEN = "main/typescript";

    /**
     * 項目番号:10<br>
     * valueobjectが格納されているサブディレクトリ
     */
    public static final String OBJECT_SUBDIRECTORY = "/valueobjectts";

    /**
     * 項目番号:11<br>
     * VeeValidateを初期化するクラス名
     */
    public static final String INIT_CLASS = "validate.ValidateInitializer";

    /**
     * 項目番号:12<br>
     * VeeValidateを初期化するメソッド名
     */
    public static final String INIT_METHOD = "init";

    /**
     * 項目番号:13<br>
     * 国際化対応したカスタムメッセージを選択する関数名
     */
    public static final String CUSTOM_MESSAGE = "customMessage";

    /**
     * 項目番号:14<br>
     * VeeValidateConfigインタフェイスのPartial実装
     */
    public static final String VALIDATE_CONFIG = "validateConfig";
}
