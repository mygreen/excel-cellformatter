======================================
カスタマイズ方法
======================================

POICellFormatterとJXLCellFormattertとも、カスタマイズ方法が同じであるため、POIを例に説明します。

------------------------
キャッシュの制御
------------------------

Excelの書式をパースする処理はコストがかかるため、一度パースした結果のフォーマッタオブジェクト *CellFormatter* は、メモリ上にキャッシュします。

^^^^^^^^^^^^^^^^^^^^^^^^^^^
キャッシュへの自動登録制御
^^^^^^^^^^^^^^^^^^^^^^^^^^^

メソッド *POICellFormatter.setCache(true|false)* でキャッシュへの自動登録を制御することができます。

* 初期値は *true* で、キャッシュへの自動登録が有効になっています。
* この機能を無効にするには、値を *false* に設定します。

.. sourcecode:: java
    
    POICellFormatter cellFormatter = new POICellFormatter();
    
    // キャッシュへの自動登録をOFFにする。
    cellFormatter.setCache(false);
    

^^^^^^^^^^^^^^^^^^^^^^^^^^^
キャッシュの初期化
^^^^^^^^^^^^^^^^^^^^^^^^^^^

キャッシュの内容を初期化などするには、 *POICellFormatter.getFormatterResolver()* で取得できる *FormatterResolver* クラスのインスタンスを利用します。

* キャッシュの内容を全てクリアするには、 *FormatterResolver.clearFormat()* メソッドを呼びます。
* 本来ライブラリで予め準備されているフォーマット *FormatterResolver.registerDefaultFormat()* メソッドで登録します。

  * *clearFormat()* メソッドを呼ぶと予め登録されているキャッシュも削除されるため、 *registerDefaultFormat()* メソッドを呼ぶことを推奨します。
  * 特に、ビルトインフォーマット（組み込みフォーマット）の場合は、書式を持たずにインデックス番号しか保持していない場合があるため、キャッシュクリ後はフォーマットに失敗する場合があります。

.. sourcecode:: java
    
    POICellFormatter cellFormatter = new POICellFormatter();
    
    // FormatterResolverの取得
    FormatterResolver formatterResolver = cellFormatter.getFormatterResolver();
    
    // キャッシュへの内容を全てクリアする。
    formatterResolver.clearFormat();
    
    // ライブラリで準備されているデフォルトのフォーマッタをキャッシュに登録する。
    formatterResolver.registerDefaultFormat();
    


----------------------
フォーマッタの事前登録
-----------------------

本来ライブラリで予め準備されているフォーマッタでは不足している場合など、*POICellFormatter.getFormatterResolver()* で取得できる *FormatterResolver* クラスのインスタンスを利用し、予め登録しておくことで制御できます。

* 特に、ビルトインフォーマットで、書式を持たずにインデックス番号しか保持していないときに有効です。
* さらに、Excelのバージョンによっても違いがある場合に、既存のフォーマッタを修正する際にも利用します。
* *LocaleSwitchFormatter* を利用することで、実行時のロケールによって切り替えることも可能です。

  * *LocaleSwitchFormatter* のコンストラクタにデフォルトのフォーマッタを渡します。
  * *LocaleSwitchFormatter.register(...)* でロケールと切り替えるフォーマッターを渡します。

.. sourcecode:: java
    
    POICellFormatter cellFormatter = new POICellFormatter();
    
    // FormatterResolverの取得
    FormatterResolver formatterResolver = cellFormatter.getFormatterResolver();
    
    // フォーマッターの作成
    CellFOrmatter fomratter1 = formatterResolver.createFormatter("yyyy-mm-dd HH:mm:ss");
    
    // インデックス番号を指定して登録する。
    formatterResolver.registerFormatter((short)2, fomratter1);
    
    // パターンを指定して登録する。
    formatterResolver.registerFormatter("yyyy/mm/dd", fomratter1);
    
    // 実行時のロケールによって切り替えることができるフォーマッタの登録
    formatterResolver.registerFormatter("[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy", 
            new LocaleSwitchFormatter(formatterResolver.createFormatter("[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy"))
                .register(formatterResolver.createFormatter("yyyy\"年\"m\"月\"d\"日\""), Locale.JAPAN, Locale.JAPANESE, LOCALE_JAPANESE));
       
    cellFormatter.format(cell, Locale.JAPAN);


