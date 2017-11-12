package com.nemezis.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.form.TextField;


/**
 * 
 * @author Nemezis
 * 
 *         Основной контент приложения , форма для работы с файлами
 *
 */

public class ContentW implements IsWidget {

	// RPC
	@SuppressWarnings("unused")
	private GeneralServiceAsync generalService;

	// Форма (аналог из HTML)
	private FramedPanel panel;
	private FormPanel form;

	// Скрытое поле для передачи параметров по HTTP
	private Hidden pathFile;

	// Название файла
	private String fileName;
	
	// URL для вызова сервлета
	// Требуется название приложения в нижнем регистре
	private String URL = GWT.getModuleBaseURL().replace("filetransferwildfly/", "");  //  MANUAL! app name
	
	// Ширина кнопок
	private String buttonWidth = "130";
	
	// Конструктор виджета
	public ContentW(GeneralServiceAsync generalService) {
		super();
		this.generalService = generalService;
	}

	// Инициализация
	@Override
	public Widget asWidget() {
		if (panel == null) {

			// Создаем окно ожидания
			AutoProgressMessageBox waitBox = new AutoProgressMessageBox(toCricket("Окно ожидания"), () -> {
				return "Подождите, пока данные загрузятся.";
			});

			// Проводник - для указания нужного файла
			final FileUploadField file = new FileUploadField();

			// Кнопка сброс
			TextButton resetButton = new TextButton("C6poc");
			resetButton.setWidth(buttonWidth);
			resetButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					form.reset();
					file.reset();
				}
			});

			VerticalLayoutContainer vlc = new VerticalLayoutContainer();
			vlc.add(new FieldLabel(file, "Выбранный файл"), new VerticalLayoutData(1, -1));

			// Заполняем параметры формы: сервлет исполнения и название файла, метод POST
			form = new FormPanel();			
			form.setAction(URL + "fileupload");

			// Скрытое поле для передачи названия файла к качестве параметра
			pathFile = new Hidden();
			vlc.add(pathFile);

			form.setEncoding(Encoding.MULTIPART);
			form.setMethod(Method.POST);
			form.add(vlc, new MarginData(10));

			// Событие успешной загрузки файла
			form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					// Проверяем результат работы метода POST
					if (event.getResults().contains("ERROR 500")) {
						dialog("Файл не загружен!");
					} else {
						// В зависимости от кода файла вызываем соответствующий
						dialog("Файл успешно загружен!");
					}
					// Скрываем окно ожидания
					waitBox.hide();
				}
			});

			// Кнопка загрузить файл
			TextButton UploadButton = new TextButton("Загрузить файл");
			UploadButton.setWidth(buttonWidth);

			UploadButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {

					// Окно ожидание
					waitBox.setProgressText("Загрузка...");
					waitBox.auto();
					waitBox.show();

					// Устанавливаем параметры для сервлета
					// Название сохраняемого файла
					pathFile.setName("pathFile");
					// Передаем название файла в сервлет
					fileName = file.getValue();
					pathFile.setValue(fileName);					
					form.submit();
				}
			});
			
			// 
			Label labelDownFile = new Label("Название файла для скачивания: ");
			
			// Поле для ввода названия скачиваемого файла
			TextField TF = new TextField();
			TF.setWidth("200");

			// Кнопка скачать файл
			TextButton DownloadButton = new TextButton("Скачать файл");
			DownloadButton.setWidth(buttonWidth);

			DownloadButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					String fName = TF.getText();
					// Выполняем сервлет 
					Window.Location.replace(URL + "filedownload/" + fName);
				}
			});

			// Добавлем элементы на панель
			panel = new FramedPanel();
			panel.setHeading("Работа с файлами");
			panel.setHeaderVisible(true);
			panel.setButtonAlign(BoxLayoutPack.CENTER);
			panel.getElement().getStyle().setBorderWidth(1, Unit.PX);
			panel.setBorders(true);
			panel.add(form);				
			panel.addButton(UploadButton);		
			panel.addButton(labelDownFile);		
			panel.addButton(TF);		
			panel.addButton(DownloadButton);		
			panel.addButton(resetButton);
		}
		return panel;
	}

	// Окно для вывода сообщений об ошибках пользователю
	public void dialog(String mes) {
		ConfirmMessageBox messageBox = new ConfirmMessageBox("Внимание! ", mes);
		messageBox.setPredefinedButtons(PredefinedButton.OK);
		messageBox.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				messageBox.hide();
			}
		});
		messageBox.setModal(true);
		messageBox.show();
	}

	// Генерация SafeHtml для шрифта Cricket
	public static SafeHtml toCricket(String title) {
		SafeHtml ret = () -> {
			return "<span style='font-family: Cricket Bold, arial; " + title + "</span>";
		};
		return ret;
	}
}