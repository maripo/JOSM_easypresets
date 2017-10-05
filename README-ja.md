# JOSM EasyPresets Plugin

簡単にプリセットを作って使用することができます。このプラグインは "プリセット" メニューに新しいメニューを追加します。
また、JOSMのプリセットとして使うことのできるXMLをエクスポートすることが可能です。
サポートしているのはプリセットファイル仕様の一部ですが、配布用プリセットの作成が少し楽になります。

![プリセットの編集](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/preset_editor.png)
 

## プリセットを作成する
* 元にしたいノードやウェイを選択した状態で "Create Preset" を選択します。
* プリセット作成ダイアログが開きます。不要なタグは "Use" 欄にチェックに入れればプリセットに含まれません。
* このプラグインは3通りのタグ設定をサポートしています。
	* "Textbox" を選べばテキストボックスになります。デフォルト値を設定することができます。
	* "Fixed value" を選べばキーと値のペアが自動で入るようになります。
	* "Selection" を選ぶと、複数の選択肢から値を選べるようにできます。「選択肢を編集...」ボタンをクリックすると選択肢編集ダイアログが開きます。1行ごとに選択肢を入力してください。![選択肢の編集](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/options.png)
* 作成したプリセットは後で編集することができます。
* ノード・ウェイ・閉じたウェイなどの中から適用対象を指定することができます。対象にしたいタイプにチェックを入れてください。![適用対象を選ぶ](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/target_types.png)
* プリセットにはアイコンを設定することができます。「アイコン選択...」ボタンを押し、好きなアイコンを選んでください。![アイコン選択](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/icon_picker.png)

## 作ったプリセットを使う
* 作成したプリセットは一般のプリセットと同様に使用することができます。検索の対象にもなります。
* 作成したプリセットはすべて "プリセット > Custom presets" メニューに表示されます。

## プリセットを管理する
* "プリセット > Manage custom presets" メニューを選ぶと、プリセットを編集・削除・エクスポート・並び替えできます。

![プリセットの管理](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/manager.png) 

## その他
* このプラグインは作成したカスタムプリセットをJOSMのユーザデータディレクトリに "EasyPresets.xml" というファイル名で保存します。
* 作成したプリセットのソートやグループ分けは今のところサポートしておりませんが、今後サポート予定です。

## 開発者
Maripo GODA <goda.mariko@gmail.com>
* Twitter @MaripoGoda
* Blog http://blog.maripo.org
* OpenStreetMap maripogoda (秋葉原〜上野エリアでマッピングしてます)