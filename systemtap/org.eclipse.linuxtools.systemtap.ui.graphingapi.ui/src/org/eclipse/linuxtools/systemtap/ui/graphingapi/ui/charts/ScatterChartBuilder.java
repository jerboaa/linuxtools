/****************************************************************
 * Licensed Material - Property of IBM
 *
 * ****-*** 
 *
 * (c) Copyright IBM Corp. 2006.  All rights reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 *
 ****************************************************************
 */
package org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.charts;

import org.eclipse.birt.chart.api.ChartEngine;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Marker;
import org.eclipse.birt.chart.model.attribute.MarkerType;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;

import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;

import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;

import org.eclipse.birt.chart.model.type.ScatterSeries;
import org.eclipse.birt.chart.model.type.impl.ScatterSeriesImpl;
import org.eclipse.birt.core.framework.PlatformConfig;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.adapters.IAdapter;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.nonui.structures.NumberType;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.internal.GraphingAPIUIPlugin;
import org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.preferences.GraphingAPIPreferenceConstants;

/**
 * Builds bar chart.
 * 
 * @author Qi Liang
 */
@SuppressWarnings("deprecation")
public class ScatterChartBuilder extends AbstractChartWithAxisBuilder {

    /**
     * Constructor.
     * 
     * @param dataSet
     *            data for chart
     */
 
	
	protected IDeviceRenderer render = null;
    protected ChartWithAxes chart = null;
    protected AbstractChartBuilder builder = null;
    protected GeneratedChartState state = null;
 //   private boolean bFirstPaint = true;
    
    public ScatterChartBuilder(Composite parent, int style, String title,IAdapter adapter) {
    super(adapter, parent, style);
    this.title = title;
    this.parent = parent;
    try {
        // INITIALIZE THE SWT RENDERING DEVICE
        //PluginSettings ps = PluginSettings.instance();
        PlatformConfig pf = new PlatformConfig();
       	ChartEngine ce = ChartEngine.instance(pf);
      	 render = ce.getRenderer("dv.SWT");
     } catch (Exception e) {
//      e.printStackTrace();
     }
     this.addPaintListener(new painter());
    }
    
   private class  painter implements PaintListener {
        /**
         * The SWT paint callback
         */
        public void paintControl(PaintEvent pe)
        {
         if (chart == null) return;
           try{
           render.setProperty(IDeviceRenderer.GRAPHICS_CONTEXT, pe.gc);
            Composite co = (Composite) pe.getSource();
            Rectangle re = co.getClientArea();
            Bounds bo = BoundsImpl.create(re.x, re.y, re.width, re.height);
            bo.scale(72d / render.getDisplayServer().getDpiResolution());
            Generator gr = Generator.instance();
       state = gr.build(render.getDisplayServer(),
               chart,
               bo,
               null,
               null,
               null);
       gr.render(render, state);
      } catch (ChartException e) {
  //     e.printStackTrace();
      }
         }
      }
    

    

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.AbstractChartBuilder#buildChart()
     */
    protected void createChart() {
    	data = adapter.getData();
        while (data.length == 0 ) {   data = adapter.getData();}
    	IPreferenceStore store = GraphingAPIUIPlugin.getDefault().getPreferenceStore();
		xSeriesTicks = store.getInt(GraphingAPIPreferenceConstants.P_X_SERIES_TICKS);
		ySeriesTicks = store.getInt(GraphingAPIPreferenceConstants.P_Y_SERIES_TICKS);
		maxItems = store.getInt(GraphingAPIPreferenceConstants.P_MAX_DATA_ITEMS);
		viewableItems = store.getInt(GraphingAPIPreferenceConstants.P_VIEWABLE_DATA_ITEMS);

        chart = ChartWithAxesImpl.create();
        
        chart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
        chart.setType("Scatter Chart");
          
        // Plot        
       //chart.getBlock( ).setBackground(ColorDefinitionImpl.WHITE( ) );        
      //  Plot p = chart.getPlot( );       
      ///  p.getClientArea( ).setBackground(GradientImpl.create( ColorDefinitionImpl.create( 225,225,255 ),ColorDefinitionImpl.create( 255, 255, 225 ),-35,false ) );      
        //p.getOutline( ).setVisible( true ); 
        // Title        cwaBar.getTitle( )                .getLabel()                .getCaption( )                .setValue( "Bar Chart with        Multiple Y Series" );//$NON-NLS-1$         
        // Legend        
        //Legend lg = cwaBar.getLegend( );        
        //lg.getText(
        //).getFont( ).setSize( 16 );        lg.getInsets( ).set( 10, 5, 0, 0 );       
       // lg.setAnchor( Anchor.NORTH_LITERAL );
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.AbstractChartBuilder#buildLegend()
     */
    protected void buildLegend() {
    	createLegend();
        chart.getLegend().setItemType(LegendItemType.SERIES_LITERAL);
        chart.getLegend().getClientArea().getOutline( ).setVisible( true );
        chart.getLegend().setVisible(true);
        chart.getLegend().getText().getFont().setSize(7);
        
    }
    
    protected void buildTitle() {
        chart.getTitle().getLabel().getCaption().setValue(title);
        chart.getTitle().getLabel().getCaption().getFont().setSize(10);
        chart.getTitle().getLabel().getCaption().getFont().setName(FONT_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildXAxis()
     */
    protected void buildXAxis() {
    	//labels = adapter.getLabels();
        xAxis = ((ChartWithAxes) chart).getPrimaryBaseAxes()[0];
        xAxis.getTitle().setVisible(true);
        xAxis.getTitle().getCaption().setValue(labels[0]);
        xAxis.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
        xAxis.setType(AxisType.TEXT_LITERAL);
        xAxis.getOrigin().setType(IntersectionType.VALUE_LITERAL);
        xAxis.getTitle().getCaption().getFont().setSize(8); 
        xAxis.getLabel().getCaption().getFont().setSize(6);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildYAxis()
     */
    protected void buildYAxis() {
        yAxis = ((ChartWithAxes) chart).getPrimaryOrthogonalAxis(xAxis);

        yAxis.getMajorGrid().getLineAttributes().setVisible(true);
       // yAxis.getMajorGrid().getLineAttributes().setColor(ColorDefinitionImpl
         //       .GREY());
        yAxis.getMajorGrid().getLineAttributes()
                .setStyle(LineStyle.DASHED_LITERAL);
        yAxis.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);

        yAxis.setType(AxisType.LINEAR_LITERAL);
        yAxis.getOrigin().setType(IntersectionType.VALUE_LITERAL);
        yAxis.getLabel().getCaption().getFont().setSize(6);
        //yAxis.getScale().setStep(1.0);
        yAxis.getTitle().getCaption().getFont().setSize(8);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.chart.AbstractChartBuilder#buildXSeries()
     */
    protected void buildXSeries() {
    	
          data = adapter.getData();
          
          int starting = 0;
          if (data.length > xSeriesTicks ) starting = data.length - xSeriesTicks;
          x= new String[data.length - starting];
          
          try {
          for (int j=starting,k=0; j < data.length; j++,k++ )
           	x[k] = data[j][0].toString();
          } catch (Exception e)
          {
//        	  e.printStackTrace();
          }
        TextDataSet categoryValues = TextDataSetImpl.create(x);
       // NumberDataSet categoryValues = NumberDataSetImpl.create(new double[] { 0, 1, 2, 3 });
       
        Series seCategory = SeriesImpl.create();
       
        seCategory.setDataSet(categoryValues);
       
        // Apply the color palette
        SeriesDefinition sdX = SeriesDefinitionImpl.create();
        sdX.getSeriesPalette().update(1);
       
        xAxis.getSeriesDefinitions().add(sdX);
       
        sdX.getSeries().add(seCategory);
       }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.examples.chart.widget.AbstractChartBuilder#buildYSeries()
     */
    protected void buildYSeries() {
   try{
       
   
        int starting = 0;
        if (data.length > xSeriesTicks ) starting = data.length - xSeriesTicks;
        int yseriescount = adapter.getSeriesCount();
        NumberDataSet orthoValuesDataSet[] = new NumberDataSet[yseriescount];
        ScatterSeries ss1[] = new ScatterSeries[yseriescount];
        SeriesDefinition sdY[] = new SeriesDefinition[yseriescount]; 
        	
        
          for (int i =1; i<=yseriescount;i++)
          {y= new Double[data.length - starting];
           for (int j=starting,k=0; j < data.length; j++,k++ )
           {
           	y[k] = NumberType.obj2num(data[j][i]).doubleValue();
           	if ( max < y[k]) max = y[k];
           	if ( min > y[k]) min = y[k];
           }
           orthoValuesDataSet[i-1]= NumberDataSetImpl.create(y);
           ss1[i-1]= (ScatterSeries) ScatterSeriesImpl.create();
           ss1[i-1].setDataSet(orthoValuesDataSet[i-1]);
          //bs1[i-1].getDisplayName().
          //bs1[i-1].getLabel().setVisible(true);
          //bs1[i-1].setLabelPosition(Position.INSIDE_LITERAL);
       
            ss1[i-1].setSeriesIdentifier(labels[i]);
            ss1[i-1].getMarker().setSize(2);
            //ss1[i-1].getMarkers().
            for ( int m = 0; m < ss1[i-1].getMarkers( ).size( ); m++ )
            	        {
            	             ( (Marker) ss1[i-1].getMarkers( ).get( m ) ).setType( MarkerType.get(i));
            	             ( (Marker) ss1[i-1].getMarkers( ).get( m ) ).setSize(2);            	      
            	         }
           // ss1[i-1].getLineAttributes().setVisible(true);
            sdY[i-1] = SeriesDefinitionImpl.create();
            sdY[i-1].getSeriesPalette().update(0-i-2);
      
             yAxis.getSeriesDefinitions().add(sdY[i-1]);
             sdY[i-1].getSeries().add(ss1[i-1]);
          }
        System.out.println("not bin updatedataset:" + max);
        yAxis.getScale().setStep(max/2);
        yAxis.getScale().setMax(NumberDataElementImpl.create(max));
        
   }catch (Exception e)
   {
	//   e.printStackTrace();
   }
    }
    
    
    public void updateDataSet() {
        // Associate with Data Set
    	try{
    	   data = adapter.getData();
    	   int starting = 0;
           if (data.length > xSeriesTicks ) starting = data.length - xSeriesTicks; 
           
    	
           x= new String[data.length - starting];
          for (int j=starting,k =0; j < data.length; j++,k++ )
          	x[k] = data[j][0].toString();
          // X-Axis
          TextDataSet categoryValues = TextDataSetImpl.create(x);
          
          Axis xAxisPrimary = chart.getPrimaryBaseAxes()[0];
          SeriesDefinition sdX = (SeriesDefinition) xAxisPrimary
                  .getSeriesDefinitions().get(0);
          ((Series) sdX.getSeries().get(0)).setDataSet(categoryValues);

          // Y-Axis
          Axis yAxisPrimary = chart.getPrimaryOrthogonalAxis(xAxisPrimary);
       //   chart.getOrthogonalAxes(xAxisPrimary, arg1)
          
          
       NumberDataSet orthoValuesDataSet1[] = new NumberDataSet[adapter.getSeriesCount()];
       for (int i=1; i<=adapter.getSeriesCount();i++)
       {
       y= new Double[data.length - starting];
       for (int j=starting,k=0; j < data.length; j++,k++ )
       {
       	y[k] = NumberType.obj2num(data[j][i]).doubleValue();
       	if ( max < y[k]) max = y[k];
       }
       
       orthoValuesDataSet1[i-1]= NumberDataSetImpl.create(y);
       SeriesDefinition sdY = (SeriesDefinition) yAxisPrimary
       .getSeriesDefinitions().get(i-1);
       ((Series) sdY.getSeries().get(0)).setDataSet(orthoValuesDataSet1[i-1]);

       }
               yAxis.getScale().setStep(max/5);
        yAxis.getScale().setMax(NumberDataElementImpl.create(max));
    	}catch(Exception e)
    	{
    //		e.printStackTrace();
    	}
    }



public void handleUpdateEvent() {
	// TODO Auto-generated method stub
	
	try{
	updateDataSet();
	repaint();
	}catch(Exception e)
	{
		//e.printStackTrace();
	}
}

public synchronized void repaint() {
    getDisplay().syncExec(new Runnable() {
            boolean stop = false;
            public void run() {
                    if(stop) return;
                    try {
                            redraw();
                    } catch (Exception e) {
                            stop = true;
                    }
            }
    });
}

public void setScale(double scale) {
 	IPreferenceStore store = GraphingAPIUIPlugin.getDefault().getPreferenceStore();
	xSeriesTicks = store.getInt(GraphingAPIPreferenceConstants.P_X_SERIES_TICKS);
	xSeriesTicks = (int) (((Integer)xSeriesTicks).doubleValue() * scale);
	handleUpdateEvent();
}


protected void createLegend() {
	labels = adapter.getLabels();
	String[] labels2 = new String[labels.length-1];
//	Color[] colors = new Color[labels2.length];

	for(int i=0; i<labels2.length; i++) {
		labels2[i] = labels[i+1];
	//	colors[i] = new Color(this.getDisplay(), IGraphColorConstants.COLORS[i]);
	}
	
	//legend = new GraphLegend(this, labels2, colors);
}

String x[];
Double y[];
boolean fullUpdate;
Object data[][];
Composite parent = null;
String labels[];
protected static int xSeriesTicks;
protected static int ySeriesTicks;
protected static int maxItems;
protected static int viewableItems;
public static final String ID = "org.eclipse.linuxtools.systemtap.ui.graphingapi.ui.charts.scatterchartbuilder";
Double min = 0.0;
Double max = 0.0;

}
