package com.msco.mil.client.tan.client;

import java.util.ArrayList;
import java.util.List;
 
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.HttpProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.JsonReader;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
//import com.msco.mil.client.com.sencha.gxt.explorer.client.model.Example.Detail;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
 
public class JsonGridExample implements IsWidget, EntryPoint {
 
 
  private FramedPanel panel;
 
  public interface EmailAutoBeanFactory extends AutoBeanFactory {
    AutoBean<RecordResult> items();
    AutoBean<ListLoadConfig> loadConfig();
  }
 
  public interface Email {
    String getName();
    String getEmail();
    String getPhone();
    String getState();
    String getZip();
  }
 
  /**
   * Defines the structure of the root JSON object being returned by the server.
   * This class is needed as we cannot return a list of objects. Instead, we
   * return a single object with a single property that contains the data
   * records.
   */
  public interface RecordResult {
    List<Email> getRecords();
  }
 
  class DataRecordJsonReader extends JsonReader<ListLoadResult<Email>, RecordResult> {
    public DataRecordJsonReader(AutoBeanFactory factory, Class<RecordResult> rootBeanType) {
      super(factory, rootBeanType);
    }
 
    @Override
    protected ListLoadResult<Email> createReturnData(Object loadConfig, RecordResult incomingData) {
      return new ListLoadResultBean<Email>(incomingData.getRecords());
    }
  }
 
  interface EmailProperties extends PropertyAccess<Email> {
    @Path("name")
    ModelKeyProvider<Email> key();
    ValueProvider<Email, String> name();
    ValueProvider<Email, String> email();
    ValueProvider<Email, String> phone();
    ValueProvider<Email, String> state();
    ValueProvider<Email, String> zip();
  }
 
  public void onModuleLoad() {
    RootPanel.get().add(this);
  }
 
  public Widget asWidget() {
    if (panel == null) {
      EmailAutoBeanFactory factory = GWT.create(EmailAutoBeanFactory.class);
      DataRecordJsonReader reader = new DataRecordJsonReader(factory, RecordResult.class);

      String path = "data/data.json";
      RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, path);
      HttpProxy<ListLoadConfig> proxy = new HttpProxy<ListLoadConfig>(builder);
 
      final ListLoader<ListLoadConfig, ListLoadResult<Email>> loader = new ListLoader<ListLoadConfig, ListLoadResult<Email>>(
          proxy, reader);
      loader.useLoadConfig(factory.create(ListLoadConfig.class).as());
 
      EmailProperties props = GWT.create(EmailProperties.class);
 
      ListStore<Email> store = new ListStore<Email>(props.key());
      loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, Email, ListLoadResult<Email>>(store));
 
      ColumnConfig<Email, String> cc1 = new ColumnConfig<Email, String>(props.name(), 100, "Sender");
      ColumnConfig<Email, String> cc2 = new ColumnConfig<Email, String>(props.email(), 165, "Email");
      ColumnConfig<Email, String> cc3 = new ColumnConfig<Email, String>(props.phone(), 100, "Phone");
      ColumnConfig<Email, String> cc4 = new ColumnConfig<Email, String>(props.state(), 50, "State");
      ColumnConfig<Email, String> cc5 = new ColumnConfig<Email, String>(props.zip(), 65, "Zip Code");
 
      List<ColumnConfig<Email, ?>> l = new ArrayList<ColumnConfig<Email, ?>>();
      l.add(cc1);
      l.add(cc2);
      l.add(cc3);
      l.add(cc4);
      l.add(cc5);
      ColumnModel<Email> cm = new ColumnModel<Email>(l);
 
      Grid<Email> grid = new Grid<Email>(store, cm);
      grid.getView().setForceFit(true);
      grid.setLoader(loader);
      grid.setLoadMask(true);
      grid.setBorders(true);
      grid.getView().setEmptyText("Please hit the load button.");
 
      panel = new FramedPanel();
      panel.setHeadingText("Json Grid Example");
      panel.setCollapsible(true);
      panel.setAnimCollapse(true);
      panel.setWidget(grid);
      panel.setPixelSize(575, 350);
      panel.addStyleName("margin-10");
      panel.setButtonAlign(BoxLayoutPack.CENTER);
      panel.addButton(new TextButton("Load Json", new SelectHandler() {
 
        public void onSelect(SelectEvent event) {
          loader.load();
        }
      }));
    }
 
    return panel;
  }
 
}
