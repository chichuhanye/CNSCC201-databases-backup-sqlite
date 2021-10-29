// STUBBED FILE
import java.sql.*;
import java.util.*; 

// This is the class where all the Database calls go
public class DbUser extends DbBasic {

	private ResultSet rs = null;

	/*
	 * Creates connection to the database with the database name as a parameter
	 */
	DbUser ( String dbName ) {
		super( dbName );
	}
	
	public void nulls(String tabName){
		try {
			//gets a list of columns
			DatabaseMetaData md = con.getMetaData();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabName);
			ResultSetMetaData rsmd = rs.getMetaData();
			int nullable = rsmd.isNullable(1);
			if(nullable == rsmd.columnNullable) {
				System.out.println("NULL");
			}
			else{
				System.out.println("NOT NULL");
			}

		/*while (rs.next()) {
				String catalog = rs.getString("TABLE_CAT");
				String table = rs.getString("TABLE_NAME");
				String column = rs.getString("COLUMN_NAME");
				String type = rs.getString("TYPE_NAME");
				int size = rs.getInt("COLUMN_SIZE");
				System.out.println("     "+ column + " " + type + ", ");
			}*/
	}
	catch(Exception e) {};
	}
	
	//Gets the database details - name, version, URL, username
	public void getDatabaseDetails(){
		try {
			//gets a list of tables
			DatabaseMetaData md = con.getMetaData(); //opens connection to the database to obtain meta data
			System.out.println("//" + md.getDriverName()); //gets driver name and prints to screen
			System.out.println("//" + md.getDriverVersion()); //gets driver version and prints to screen
			System.out.println("//" + md.getURL()); //gets database url
			System.out.println("//" + md.getUserName()); //gets username
			
			ResultSet rs = md.getTypeInfo(); //creates result set and calls get type information method on database metadata
			
			//Loops and gets the supported data type names supported by the database
			while (rs.next()) {
				System.out.println("//" + rs.getString("TYPE_NAME"));
			}
		}
		catch(Exception e) {};
	}
	
	//MILESTONES A-C - Gets table names and calls create table and insert into statements
	public void getTableNames(){
		try {
			//Calls a method which gets database information - driver, version information, url, username supported data types
			//getDatabaseDetails();
			//System.out.println(" ");
		
			//gets a list of tables
			DatabaseMetaData md = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = md.getTables(null, null, "%", types);
			//System.out.println("TABLES:\n");
			
			//Calls the database milestone methods on each table of database
			while (rs.next()) {
				//converts the table name to string to be processed as string parameters in methods
				String tableName = rs.getString(3);
				//nulls(tableName); //checks if columns can take null values
				//tableName = tableName.replace(" ","");
				createTable(tableName); //executes CREATE TABLE Statements
				createIndex(tableName);
				insertInto(tableName);//executes INSERT INTO Statements
				//System.out.println("");
				//createIndex(tableName); //creates indexes
			}
		}
		catch(Exception e) {};
	}
	
	public void createIndex(String table) throws SQLException {
		/*try {
			DatabaseMetaData dbmd = con.getMetaData();  //gets metadata from database connection
			ResultSet rs = dbmd.getIndexInfo(null, null, table, true, false);    //第一个改过，原来是db名
			ResultSetMetaData rsmd = rs.getMetaData();  //gets metadata from result set

			// Display the result set data.
			while (rs.next()) {
				String indexName = rs.getString("INDEX_NAME");//以提取索引名称
				Boolean nonUnique = rs.getBoolean("NON_UNIQUE");//提取唯一信息
				Short type = rs.getShort("TYPE");//以提取索引类型
				int ordinalPosition = rs.getInt("ORDINAL_POSITION");//以提取顺序位置
				String columnName = rs.getString("COLUMN_NAME");

				if (!indexName.startsWith("sqlite")) {
					System.out.println("CREATE INDEX " + indexName + " ON " + table + " ( " + columnName + " );");
				}

				*//*for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (indexName.startsWith("sql")) {
						System.out.println("");
					} else {
						//Prints out the CREATE INDEX statements with table name and column values inserted
						System.out.println("CREATE INDEX " + indexName + " ON " + table + " (" + rs.getString(i) + " );");
					}

				}*//*
				System.out.println(" "); //prints spacing
				rs.close();  //closes result set
			}
			//Catches exception
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet indexRs=dbmd.getIndexInfo(null,null,table,true,false);
		Map<String,String> map=new HashMap();
		while (indexRs.next()) {
			String indexName=indexRs.getString("INDEX_NAME");//以提取索引名称
			Boolean nonUnique=indexRs.getBoolean("NON_UNIQUE");//提取唯一信息
			//Short type=indexRs.getShort("TYPE");//以提取索引类型
			//int ordinalPosition=indexRs.getInt("ORDINAL_POSITION");//以提取顺序位置
			String columnName=indexRs.getString("COLUMN_NAME");
			if (!indexName.startsWith("sqlite")) {
				if (!map.containsKey(indexName + "----" + nonUnique)) {
					map.put(indexName + "----" + nonUnique, "'" + columnName + "'");
				} else {
					map.put(indexName + "----" + nonUnique, map.get(indexName + "----" + nonUnique) + ",'" + columnName + "'");
				}
			}
		}
		for(String key:map.keySet()){
			String content = "";
			String[] va=key.split("----");
			if (va[1].equals("false")){
				//唯一索引
				content=content+"CREATE UNIQUE INDEX '"+va[0]+"' ON '"+table+"' ("+map.get(key)+");";
			}else {
				//不唯一索引
				content=content+"CREATE  INDEX '"+va[0]+"' ON '"+table+"' ("+map.get(key)+");";
			}
			System.out.println(content);
		}
	}

	//MILESTONE C - CREATE TABLE STATEMENTS
	public void createTable(String tabName){
		try {
			//gets a list of columns
			DatabaseMetaData md = con.getMetaData(); //gets metadata from database connection
			ResultSet rs = md.getColumns(null, null, tabName, null); //gets the columns from each table using db metadata
			//System.out.println("DROP TABLE IF EXISTS " + tabName +";"); //prepares database for new tables to be inserted, removing potential duplicate tables
			//System.out.print("CREATE TABLE IF NOT EXISTS " + tabName + "("); //Prints statement to create tables
			//System.out.println(" "); 11

			System.out.println("DROP TABLE IF EXISTS " + "\"" + tabName + "\"" +";"); //prepares database for new tables to be inserted, removing potential duplicate tables
			System.out.print("CREATE TABLE IF NOT EXISTS " + "\"" + tabName + "\"" + "("); //Prints statement to create tables
			
			//Loops through all columns and prints all column names and types for all tables
			while (rs.next()) {
				String catalog = rs.getString("TABLE_CAT");
				String table = rs.getString("TABLE_NAME");
				String column = rs.getString("COLUMN_NAME");
				String type = rs.getString("TYPE_NAME");
				int size = rs.getInt("COLUMN_SIZE");

				//System.out.println("     "+ column + " " + type + ", "); 11
				System.out.print("     "+ column + " " + type + ", ");
			}
		}
		catch(Exception e) {};
		getPrimaryKeys(tabName); //calls method to get the primary keys of the table
		getForeignKeys(tabName); //calls method to get the foreign keys of the table
		//System.out.println(" "); 11
		System.out.println(");"); //prints end of create table statement
		//System.out.println(" "); //prints new line 11
	}
	
	//MILESTONE C - GETS PRIMARY KEYS FOR USE IN CREATE TABLE STATEMENTS
	public void getPrimaryKeys(String tabName){
		try {
				//gets a list of columns
				DatabaseMetaData md = con.getMetaData();
				ResultSet tables = md.getTables(null, null, tabName, new String[] { "TABLE" });
				String catalog = tables.getString("TABLE_CAT");
				String schema = tables.getString("TABLE_SCHEM");
				String tableName = tables.getString("TABLE_NAME");
				boolean firstTime = true;
				System.out.print("     ");
				System.out.print("PRIMARY KEY (");
			
				try (ResultSet primaryKeys = md.getPrimaryKeys(catalog, schema, tableName)) {
					while (primaryKeys.next()) {
						if(firstTime==false){
							System.out.print(", ");
						}
						System.out.print(primaryKeys.getString("COLUMN_NAME"));
						if(firstTime==true){
							firstTime = false;
						}  
					}
				}
		}
		catch(Exception e) {};
		System.out.print(")");
	}
		
	public void getForeignKeys(String tabName){
		try {
				boolean firstFK = true;
				boolean pkPresent = true;

				ResultSet rs = null;
				DatabaseMetaData meta = con.getMetaData();
				rs = meta.getImportedKeys(null, null, tabName);
				
				while (rs.next()) {
					//Gets foreign key table and column names
					//String foreignTableName = rs.getString("FKTABLE_NAME");
					String foreignColumnName = rs.getString("FKCOLUMN_NAME");
					//Gets the primary key table and column name referenced by the foreign keyString pkTableName = rs.getString("PKTABLE_NAME");
					String primaryTableName = rs.getString("PKTABLE_NAME");
					String primaryColumnName = rs.getString("PKCOLUMN_NAME");
					//int fkSequence = rs.getInt("KEY_SEQ");
					
					//对外键的输出格式进行改动
					if(pkPresent==true){
						System.out.print(", "); //prints comma
						pkPresent = false; //sets pkPresent to false
					} 

					if(firstFK==false){
						System.out.print(", "); //prints comma
						firstFK = true; //sets firstFK to true
					}

					System.out.print("     ");
					System.out.print("FOREIGN KEY ("+ foreignColumnName +") REFERENCES " + primaryTableName + "(" + primaryColumnName + ")");
					
					if(firstFK==true){
						firstFK = false;
					} 
				}
		}
		catch(Exception e) {};
	}
	
	//MILESTONE A/B - INSERT INTO STATEMENTS
	public void insertInto(String tabName){
		try {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " +  "\"" + tabName+ "\"");
				ResultSetMetaData rsmd = rs.getMetaData();
				List<String> columnList = new ArrayList<String>(rsmd.getColumnCount()); //保存列名
				boolean firstValue = true;
				
				//加入列名
				for(int i = 1; i <= rsmd.getColumnCount(); i++){
					columnList.add(rsmd.getColumnName(i));
				}
							
				//获取值
				List<String> values = new ArrayList<String>(rsmd.getColumnCount());
		
				while (rs.next()) {
					System.out.print("INSERT INTO " + "\"" + tabName + "\"" + " VALUES (");
					

					for(int i = 1; i <= rsmd.getColumnCount(); i++){	//单引号要换成两个单引号！  还有Chinook数据里面的分号
						values.add(rs.getString(i));
						if(firstValue==false){
							System.out.print(", ");
						}
						if (rs.getString(i) !=null){
							ArrayList currentText = new ArrayList<>(Arrays.asList(rs.getString(i).split("\r\n")));
							String data = "";
							if(currentText.size()>1){

								for(Object str:currentText){
									data += str;
								}
							}else{data = rs.getString(i);}


						System.out.print("'" + data.replace("'","''") + "'");}
						if (rs.getString(i)==null)System.out.print("'"+"'");
						if(firstValue==true){
							firstValue = false;
						}
					}
					firstValue = true;
					System.out.println(");");

				}
			rs.close(); //closes result set
			stmt.close(); //closes statement
		}
		//Catches exception
		catch (Exception e) {
			System.out.println(e);
		}
	}
}
