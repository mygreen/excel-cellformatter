------------------------------------------------
ファイル内部の日付の基準日の設定
------------------------------------------------

Excelは、日付情報をファイル内部では数値型として値を保持しており、値が0の場合は基準日として、通常は **「1899年12月31日（Excel表記上は1900年1月0日）」** を表します。

この基準日は、設定によって変更することができ、ファイルごとに設定を持ちます。

* `Miscrosoftのサポートページ <https://support.microsoft.com/ja-jp/kb/180162/ja>`_


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Excelの1904年始まりの設定変更方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Excelのバージョンによって設定方法は変わります。

* Excel2000～2003の場合、メニュー「ツール」-「オプション」から表示されるダイアログ中のタブ「計算方法」から設定できます。
* Excel2007の場合、Officeボタンの「オプション」から表示されるダイアログ中のペイン「詳細設定」から設定できます。
* Excel2010の場合、メニュー「ファイル」の「オプション」から表示されるダイアログ中のペイン「詳細設定」から設定できます。
* LibreOfficeの場合、メニュー「ツール」-「オプション」から表示されるダイアログ中のペイン「LibreOffice Calc」-「計算式」から設定できます。

.. figure:: ./_static/excel_settings_date1904_2002.png
   :scale: 50%
   :align: center
   
   Excel2002(XP)の場合の1904年始まりの設定

.. figure:: ./_static/excel_settings_date1904_2010.png
   :scale: 50%
   :align: center
   
   Excel2010の場合の1904年始まりの設定


.. figure:: ./_static/excel_settings_date1904_libre.png
   :scale: 50%
   :align: center
   
   LibreOfficeの場合の1904年始まりの設定



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ライブラリによる1904年始まりの判定方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Apache POIにおける1904年始まりの判定方法
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Apache POIの場合、ファイルのフォーマットによって取得方法が変わります。

* 公開されていないメソッドなのでリフレクションを使用して取得します。
* 本ライブラリでは、 *POICell#isDateStart1904()* 中のメソッドで実装されています。
* 1904年始まりかどうかの設定はファイル単位に持つため、Workbookを取得して判定を行います。

.. sourcecode:: java
    
    final Workbook workbook = cell.getSheet().getWorkbook();
    if(workbook instanceof HSSFWorkbook) {
        // 拡張子がxlsの場合
        try {
            Method method = HSSFWorkbook.class.getDeclaredMethod("getWorkbook");
            method.setAccessible(true);
            
            InternalWorkbook iw = (InternalWorkbook) method.invoke(workbook);
            return iw.isUsing1904DateWindowing();
            
        } catch(NoSuchMethodException | SecurityException e) {
            logger.warn("fail access method HSSFWorkbook.getWorkbook.", e);
            return false;
        } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.warn("fail invoke method HSSFWorkbook.getWorkbook.", e);
            return false;
        }
        
    } else if(workbook instanceof XSSFWorkbook) {
        // 拡張子がxlsxの場合
        try {
            Method method = XSSFWorkbook.class.getDeclaredMethod("isDate1904");
            method.setAccessible(true);
            
            boolean value = (boolean) method.invoke(workbook);
            return value;
            
        } catch(NoSuchMethodException | SecurityException e) {
            logger.warn("fail access method XSSFWorkbook.isDate1904.", e);
            return false;
        } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.warn("fail invoke method XSSFWorkbook.isDate1904.", e);
            return false;
        }
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
JExcelAPIにおける1904年始まりの判定方法
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

JExcel APIの場合も同様にWorkbookから取得しますが、セルクラスから上位のSheetクラスなどを取得できないため不便です。

* 公開されていないメソッドなのでリフレクションを使用して取得します。
* 本ライブラリでは、 *JXLUtils#isDateStart1904(...)* 中のメソッドで実装されています。
* 1904年始まりかどうかの設定はシートにも引き継がれるため、Sheet、Wookbookのどちらからも判定できます。


.. sourcecode:: java
    
    // Workbookを元に判定を行う場合
    public static boolean isDateStart1904(final Workbook workbook) {
        ArgUtils.notNull(workbook, "workbook");
        
        if(workbook instanceof WorkbookParser) {
            try {
                Field field = WorkbookParser.class.getDeclaredField("nineteenFour");
                field.setAccessible(true);
                
                boolean value = field.getBoolean(workbook);
                return value;
                
            } catch (NoSuchFieldException | SecurityException e) {
                logger.warn("fail access field WrokbookParser#nineteenFour", e);
                return false;
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.warn("fail invoke field WrokbookParser#nineteenFour", e);
                return false;
            }
            
        }
        
        return false;
    }
    
    // Sheetを元に判定を行う場合
    public static boolean isDateStart1904(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        
        if(sheet instanceof SheetImpl) {
            try {
                Field field = SheetImpl.class.getDeclaredField("nineteenFour");
                field.setAccessible(true);
                
                boolean value = field.getBoolean(sheet);
                return value;
                
            } catch (NoSuchFieldException | SecurityException e) {
                logger.warn("fail access field SheetImpl#nineteenFour", e);
                return false;
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.warn("fail invoke field SheetImpl#nineteenFour", e);
                return false;
            }
            
        }
        
        return false;
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
日時の内部表現
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Excelの場合、日時ファイル内部では数値として値を保持し、次の仕様となっています。

* 整数部が日付情報。
  
  * 1日は、値は1.0。

* 小数部が時刻情報。

  * 1秒は、 ``1/(24*60*60)≒1.15741e-5`` 。
  * Excelは、秒までの精度しか持たない。 

* タイムゾーンは持たない。
  
  * タイムゾーンは持たないため、OSの言語環境など変わっても、表示される時間は変わりません。

* 基準日の値は、 ``0.0`` となる。

  * ファイルの基準日の設定により、値が ``0.0`` のとき、1900年1月0日始まりか1904年1月1日始まりか変わります。
  * 内部的な値が負の数となる、基準日より前の日時は表現できない。

* 1900年始まりの場合、他のオフィスアプリと互換性を持たすために、以下の不良のような仕様となっています。

  * 1900年1月0日から始まる。
  * グレゴリオ歴上は、閏年でないのに、1900年2月29日が存在する。


.. note:: グレゴリオ歴の閏年の定義
   
   `グレゴリオ暦 <https://ja.wikipedia.org/wiki/%E9%96%8F%E5%B9%B4>`_ では、次の規則に従って400年間に（100回ではなく）97回の閏年を設ける。
   
   1. 西暦年が4で割り切れる年は閏年。
   2. ただし、西暦年が100で割り切れる年は平年（閏年でない）。
   3. ただし、西暦年が400で割り切れる年は閏年。


.. list-table:: Excelの日付の内部表現
   :widths: 20 25 25 30 
   :header-rows: 1
   
   * - Excel表記上の日時
     - | 数値表現
       | 1900年始まり
     - | 数値表現
       | 1904年始まり
     - 備考
     
   * - 1900年01月00日 00時00分00秒
     - 0.00000000000 
     - 
     - 1900年始まりの基準日。
    
   * - 1900年01月01日 00時00分00秒
     - 1.00000000000 
     - 
     - 基準日から1日進む。
    
   * - 1900年02月29日 00時00分00秒
     - 60.00000000000 
     - 
     - 正しくない閏日。
    
   * - 1900年03月01日 00時00分00秒
     - 61.00000000000 
     - 
     - 正しくない閏日の翌日。
    
   * - 1904年01月01日 00時00分00秒
     - 1462.00000000000 
     - 0.00000000000 
     - 1904年始まりの基準日。
    
   * - 1970年01月01日 00時00分00秒
     - 25569.00000000000 
     - 24107.00000000000
     - UTCの基準日。
    
   * - YYYY年MM月DD日 00時00分01秒
     - 0.0000115741
     - 0.0000115741
     - 基準日から1秒経過
    
   * - YYYY年MM月DD日 00時01分00秒
     - 0.00069444444444
     - 0.00069444444444
     - 基準日から1分経過
    
   * - YYYY年MM月DD日 00時01分00秒
     - 0.04166666666667
     - 0.04166666666667
     - 基準日から1時間経過
    
   * - YYYY年MM月DD日 23時59分59秒
     - 0.99998842592593
     - 0.99998842592593
     - 基準日から23時間59分59秒経過
    


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Excel上の日時の値をUTC表記に変換する方法
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Excelは日時は内部では数値で保持しており、Javaの ``java.util.Date`` に渡す形式であるUTC表現とは異なる。
また、基準日が複数あるため、UTC形式に変換するには次の計算を行う。

本ライブラリでは、 ``ExcelDateUtils#convertJavaDate(...)`` メソッドで提供されています。

1. 1904年始まりの場合。

  a. 24107の値を足す。(1970年1月1日から見て、1904年1月1日の経過日数を足す。)

2. 1900年始まりの場合。

  a. 値が61.0以上ならば、1.0を引く。(1900年2月29日はUTC上存在しないため、1900年3月1日（=61.0）以降は、1日多くなるので調整する。)
  
  b. 25568.0の値を足す。(1970年1月1日から見て、1900年1月0日(=1899年12月31日)の経過日数を足す。)

3. 値に24*60*60*1000を掛ける。(Excelは時刻が小数部なので、24*60*60で整数1桁目の精度を秒にする。UTCはミリ秒精度なので、さらに1000を掛ける。)

