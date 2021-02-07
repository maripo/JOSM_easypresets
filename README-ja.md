# JOSM EasyPresets Plugin

[English](https://github.com/maripo/JOSM_easypresets/blob/master/README.md)
[ハンガリー語](https://github.com/maripo/JOSM_easypresets/blob/master/README-hu.md)

簡単にプリセットを作って使用することができます。このプラグインは "プリセット" メニューに新しいメニューを追加します。
また、JOSMのプリセットとして使うことのできるXMLをエクスポートすることが可能です。
サポートしているのはプリセットファイル仕様の一部ですが、配布用プリセットの作成が少し楽になります。

![プリセットの編集](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/preset_editor.png)
 

## プリセットを作成する
* 元にしたいノードやウェイを選択した状態で "Create Preset" を選択します。
* プリセット作成ダイアログが開きます。不要なタグは "Use" 欄にチェックに入れればプリセットに含まれません。
* このプラグインは3通りのタグ設定をサポートしています。
	* "テキストボックス : テキストボックスになります。デフォルト値を設定することができます。
	* "固定値" : キーと値のペアが自動で入るようになります。
	* "選択式 (単一選択)" : プルダウンメニューによって、複数の選択肢から値を1つ選べるようにできます。「選択肢を編集...」ボタンをクリックすると選択肢編集ダイアログが開きます。1行ごとに選択肢を入力してください。  
	![選択肢の編集](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/options.png)
	* "選択式 (複数選択)" : 選択式 (単一選択) と同様に複数の選択肢を用意し、選べるようにします。こちらはリストの中から複数の選択肢を選ぶことが可能です。
	* "チェックボックス" : チェックボックスによってyes/noを入力することができます。
* 「ダイアログにプリセット名を表示する」のチェックボックスで、プリセット適用時のダイアログにプリセット名が表示するかどうかを選べます。  
![選択肢の編集](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/show_preset_name.png)
* 「固定値」のみを持つプリセットは、「プリセット名を表示する」のチェックを外すことで、ダイアログを表示せずに即座に適用することができるようになります。
* 作成したプリセットは後で編集することができます。
* ノード・ウェイ・閉じたウェイなどの中から適用対象を指定することができます。対象にしたいタイプにチェックを入れてください。  
![適用対象を選ぶ](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/target_types.png)
* プリセットにはアイコンを設定することができます。「アイコン選択...」ボタンを押すと、既存のプリセットで使われているアイコンのリストが表示されるので、好きなアイコンを選んでください。  ![アイコン選択](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/ja/icon_picker.png)

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