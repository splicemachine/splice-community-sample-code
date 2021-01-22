from pyspark.sql.types import *
from beakerx import *
from ipywidgets import widgets, Layout, Button
from ipywidgets import interactive
from IPython.display import display, clear_output

#Begin spark session 
from pyspark.sql import SparkSession
spark = SparkSession.builder.getOrCreate()

#Create pysplice context. Allows you to create a Spark dataframe using our Native Spark DataSource 
from splicemachine.spark import PySpliceContext
splice = PySpliceContext(spark)

def display_feature_search():
   
    sql =f"""SELECT Name, Feature_Type, f.Description, ft.Schema_name, ft.Table_Name, ft.Description as SetDescription, f.Tags, f.Last_Update_TS 
        FROM FeatureStore.feature f INNER JOIN FeatureStore.feature_set ft ON ft.Feature_Set_ID=f.Feature_Set_ID
    """
    df = splice.df(sql)
    feature_data = df.toPandas()


    ############################################################################################
    searchText = widgets.Text(layout=Layout(width='80%'), description='Search:')
    display(searchText)

    def handle_submit(sender):
        table_data=feature_data[feature_data['TAGS'].str.contains(searchText.value, case=False) | feature_data['DESCRIPTION'].str.contains(searchText.value, case=False) |feature_data['SETDESCRIPTION'].str.contains(searchText.value, case=False) | feature_data['NAME'].str.contains(searchText.value, case=False)]
        table_data.reset_index(drop=True, inplace=True)
        table = TableDisplay(table_data)     
        redisplay(table)

    searchText.on_submit(handle_submit)

    def displayContinuous(data, feature, table):
        median_val = data.median()
        mean_val = data.mean()
        std_val = data.std()
        min_range_value = median_val - std_val * 3
        max_range_value = median_val + std_val * 3
        min_val = data.min()
        max_val = data.max()
        range_min = (min_range_value if min_range_value>min_val else min_val)
        range_max = (max_range_value if max_range_value<max_val else max_val) 
        print(f"mean:\t{mean_val.round(2)}\tstd:\t{std_val.round(2)}")
        print(f"median:\t{median_val.round(2)}")
        print(f"range: {min_val} to {max_val}" )
        return Histogram(data=data.tolist(), binCount=10, rangeMin = range_min, rangeMax=range_max, title=f"Feature - {table} - {feature} Distribution", xLabel=feature)

    def displayNominal(data, feature, table):
        return data.value_counts().plot(kind='pie')

    def displayOrdinal(data, feature, table):
        return Histogram(data=data.tolist(), title=f"Feature - {table} - {feature} Distribution", xLabel=feature)

    def onSelectFeature( row, col, tabledisplay):
        redisplay(tabledisplay)
        switcher = { 'O': displayOrdinal, 'C': displayContinuous, 'N':displayNominal}
        feature_name = tabledisplay.values[row][0]
        feature_type = tabledisplay.values[row][1]
        table_name = f"{tabledisplay.values[row][3]}.{tabledisplay.values[row][4]}"
        feature_sql = f"SELECT {feature_name} as FeatureValues FROM {table_name}"
        print("Acquiring feature data...")
        display_data = splice.df(feature_sql).toPandas()['FEATUREVALUES']

        # select appropriate display and display feature distribution
        display_func = switcher.get(feature_type)
        chart = display_func( display_data, feature_name, table_name)
        display(chart)

    def redisplay(td ):
        clear_output(wait=True)
        display(searchText)
        td.setDoubleClickAction(onSelectFeature)
        td.setColumnFrozen('NAME',True)
        display(td)


    table_data=feature_data
    table = TableDisplay(table_data)
    redisplay(table)
