// package kevinadi

object queryCode {

    import annotation.tailrec

    def decode(code: String): String = code match {

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        case "CLD01" => "SELECT C.NAME, T.TRACKING_ID, T.FUNDER, T.DATE, T.BROKER, T.LOAN_BALANCE, T.PAYMENT, T.TOTAL_PAY " +
                        "FROM CLIENT C JOIN TRAIL T ON T.CLIENT_ID = C.CLIENT_ID " +
                        "WHERE T.DATE LIKE '2014-%'"
        
        case "CLD02" => "SELECT * FROM TRAIL"

        case "CLD03" => "SELECT DISTINCT DATE FROM TRAIL ORDER BY DATE DESC"

        case "CLD04" => "SELECT C.NAME, T.TRACKING_ID, T.FUNDER, T.DATE, T.BROKER, T.LOAN_BALANCE, T.PAYMENT, T.TOTAL_PAY " +
                        "FROM CLIENT C JOIN TRAIL T ON T.CLIENT_ID = C.CLIENT_ID " +
                        "WHERE T.DATE>=? AND T.DATE<=?" +
                        "ORDER BY T.DATE DESC, C.NAME, T.TRACKING_ID"

        case "CLD05" => "SELECT TRAIL.DATE AS DATE, TRAIL.TRACKING_ID AS TRACKING_ID, " +
                        "CLIENT.NAME AS NAME, TRAIL.FUNDER AS FUNDER, 'EXIT' AS STATUS " +
                        "FROM CLIENT " +
                        "JOIN TRAIL ON TRAIL.CLIENT_ID = CLIENT.CLIENT_ID " +
                        "WHERE TRAIL.DATE = ? " +
                        "AND TRAIL.TRACKING_ID NOT IN " +
                        "(SELECT TRACKING_ID FROM TRAIL WHERE DATE = ?) " +
                        "UNION ALL " +
                        "SELECT TRAIL.DATE AS DATE, TRAIL.TRACKING_ID AS TRACKING_ID, " +
                        " CLIENT.NAME AS NAME, TRAIL.FUNDER AS FUNDER, 'ENTRY' AS STATUS " +
                        "FROM CLIENT " +
                        "JOIN TRAIL ON TRAIL.CLIENT_ID = CLIENT.CLIENT_ID " +
                        "WHERE TRAIL.DATE = ? " +
                        "AND TRAIL.TRACKING_ID NOT IN " +
                        "(SELECT TRACKING_ID FROM TRAIL WHERE DATE = ?) " +
                        "ORDER BY DATE, NAME"

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // All flight no
        case "JS001" => "SELECT * FROM FLIGHTS ORDER BY FLT_NO"

        // All years
        case "JS002" => "SELECT * FROM YEARS ORDER BY D_YEAR"

        // All aircraft types
        case "JS02"  => "SELECT ACFT_TYPE,C,Y,TOTAL FROM PLANES"

        // SLF year month
        case "JS05"  => "SELECT DEP_DATE, FLT_NO, STRETCH, ACFT_TYPE, C, Y, TOTAL, SLF_C, SLF_Y, SLF_TOTAL " +
                        "FROM SLF WHERE D_YEAR=? AND D_MONTH=? " +
                        "LIMIT 500"

        // SLF year month flight
        case "JS06"  => "SELECT DEP_DATE, FLT_NO, STRETCH, ACFT_TYPE, C, Y, TOTAL, SLF_C, SLF_Y, SLF_TOTAL " +
                        "FROM SLF WHERE D_YEAR=? AND D_MONTH=? AND FLT_NO=?  ORDER BY DEP_DATE " +
                        "LIMIT 500"

        // SLF year
        case "JS07"  => "SELECT DEP_DATE, FLT_NO, STRETCH, ACFT_TYPE, C, Y, TOTAL, SLF_C, SLF_Y, SLF_TOTAL " +
                        "FROM SLF WHERE D_YEAR=? " +
                        "LIMIT 500"

        // SLF year flight
        case "JS08"  => "SELECT DEP_DATE, FLT_NO, STRETCH, ACFT_TYPE, C, Y, TOTAL, SLF_C, SLF_Y, SLF_TOTAL " +
                        "FROM SLF WHERE D_YEAR=? AND FLT_NO=? ORDER BY DEP_DATE " +
                        "LIMIT 500"

        // SLF flight
        case "JS09"  => "SELECT DEP_DATE, FLT_NO, STRETCH, ACFT_TYPE, C, Y, TOTAL, SLF_C, SLF_Y, SLF_TOTAL " +
                        "FROM SLF WHERE FLT_NO = ? " +
                        "LIMIT 500"

        // Count rows
        case "JS000"  =>  "SELECT COUNT(*) AS ROWCOUNT FROM SLF"

        // Insert
        case "JS90"  => "INSERT INTO SLF(DEP_DATE, FLT_NO, STRETCH, ACFT_TYPE, C, Y, TOTAL, SLF_C, SLF_Y, SLF_TOTAL, D_YEAR, D_MONTH, D_DATE) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        ////////////////////////////////////////////////////////////////////////////////////////////////////

        case "GFD01" => "SELECT * FROM FLOWN LIMIT 100"

        case "GFD90" => ""

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        case "test" => "SELECT * FROM INFORMATION_SCHEMA.HELP"

        case _ => ""
    }

    def insertData(servletName: String, db: Jdbc, sql: String, params: Array[String]): String = 
        servletName match {
            case "JetSeater" => insertJetSeater(db, sql, params)
            case "CLD"       => insertCLD(db, sql, params)
            case "GFD"       => insertGFD(db, sql, params)
            case _           => "not implemented"
        }

    private def insertJetSeater(db: Jdbc, sql: String, params: Array[String]): String = {

        // 0: DEP_DATE, 1: FLT_NO, 2: STRETCH, 3: ACFT_TYPE, 4: C, 5: Y, 6: TOTAL, 
        // 7: SLF_C, 8: SLF_Y, 9: SLF_TOTAL, 10: D_YEAR, 11: D_MONTH, 12: D_DATE

        db.insert(sql, params) match {

            case "success" => {
            
                db.select("SELECT * FROM YEARS WHERE D_YEAR=?", Array(params(10))) match {
                    case Nil => {
                        db.insert("INSERT INTO YEARS VALUES(?)", Array(params(10)))
                        System.err.println("Inserted new year " + params(10))
                    }
                    case _ => System.err.println("Year " + params(10) + " exists")
                }
            
                db.select("SELECT * FROM FLIGHTS WHERE FLT_NO=?", Array(params(1))) match {
                    case Nil => {
                        db.insert("INSERT INTO FLIGHTS VALUES(?)", Array(params(1)))
                        System.err.println("Inserted new flight " + params(1))
                    }
                    case _ => System.err.println("Flight " + params(1) + " exists")
                }
            
                return "success"
            }
            
            case x => return "(insertJetSeater) " + x
        }
    }

    private def insertCLD(db: Jdbc, sql: String, params: Array[String]): String = {
        // import au.com.bytecode.opencsv.{CSVParser, CSVReader}
        return "CLD insert not implemented"
    }

    private def insertGFD(db: Jdbc, sql: String, params: Array[String]): String = {

        val oldFormat = new java.text.SimpleDateFormat("dd-MMM-yy")
        val newFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")

        val headers = List("FLTDAT", "ISS_DATE", "AGENT_N", "AGENT_NAME", "ALN", "TICKET_NO", "CPN", "PAXNAM", "SECTOR", "FLNO", "R", "TOUR_CD", "FBTD_CD", "STATUS", "CCY", "GROSS", "DISC", "COMM", "NETT", "YQ_VLU", "NETTT_VLU", "ITENERARY")
        val indexes = List(1,11,21,29,70,74,85,96,152,159,164,166,187,205,212,216,228,240,252,264,275,287,304)
        val slices = indexes.map(_ - 1).zip( indexes.tail.map(_ - 2) )

        val fileName = "input.txt"
        val f = io.Source.fromFile(fileName)
                         .getLines
                         .dropWhile( !_.startsWith("---------") )
                         .drop(1)

        def fixDate(d: String) = newFormat.format(oldFormat.parse(d))
        def cols(row: String) = slices.map(x => row.slice(x._1, x._2).trim)
        val rows = f.map(cols)
        val data = rows.map(x => headers.zip(x).toMap)
                       .map(x => x + ("FLTDAT"     -> fixDate(x("FLTDAT"))))
                       .map(x => x + ("ISS_DATE"   -> fixDate(x("ISS_DATE"))))
                       .map(x => x + ("GROSS"      -> x("GROSS").replace(",","")))
                       .map(x => x + ("DISC"       -> x("DISC").replace(",","")))
                       .map(x => x + ("COMM"       -> x("COMM").replace(",","")))
                       .map(x => x + ("NETT"       -> x("NETT").replace(",","")))
                       .map(x => x + ("YQ_VLU"     -> x("YQ_VLU").replace(",","")))
                       .map(x => x + ("NETTT_VLU"  -> x("NETTT_VLU").replace(",","")))

        return db.insertBatch("FLOWN", headers, data)

    }
}



object jdbcJetty {

    import org.eclipse.jetty.server.{Server, Handler}
    import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder, FilterHolder}
    import org.eclipse.jetty.webapp.WebAppContext
    import org.eclipse.jetty.server.handler.{DefaultHandler, HandlerList, ShutdownHandler}
    import org.eclipse.jetty.servlets.GzipFilter
    import javax.servlet.DispatcherType
    import java.util.EnumSet

    import java.awt.Desktop;
    import java.net.URI;

    val PORT = 62000
    val WARFILE = "ScalaCrud.war"

    def main(args: Array[String]): Unit = {

        val port = if(args.length >= 1) args(0).toInt else this.PORT
        val server = new Server(port)
        val context = new WebAppContext()

        context.setContextPath("/")
        context.setWar(if(new java.io.File(this.WARFILE).exists) this.WARFILE else "webapp")
        context.addFilter(new FilterHolder(new GzipFilter), "/*", EnumSet.of(DispatcherType.REQUEST))
        context.addServlet(new ServletHolder(new DefaultServlet),"/")
        context.addServlet(new ServletHolder(new JdbcServlet("CLD")),"/CLD")
        context.addServlet(new ServletHolder(new JdbcServlet("JetSeater")),"/JetSeater")
        // context.addServlet(new ServletHolder(new JdbcServlet("GFD")),"/GFD")
        
        val handlers = new HandlerList
        handlers.setHandlers(Array(
            new ShutdownHandler("ABC", true, true),
            context
        ))
        server.setHandler(handlers)
        
        server.start
        Desktop.getDesktop().browse(new URI("http://localhost:62000"))
        io.StdIn.readLine
        server.stop
        server.join
    }
}


class JdbcServlet(servletName: String) extends javax.servlet.http.HttpServlet {

    import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

    val db = new Jdbc("org.h2.Driver", "jdbc:h2:./" + servletName + ";AUTO_SERVER=TRUE", "sa", "")

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {

        val out = resp.getWriter

        val sql = queryCode.decode(req.getParameter("q"))
        val params = req.getParameterValues("p") match {
            case null => Array[String]()
            case x    => x
        }
        
        val startTime = System.currentTimeMillis
        val result = db.select(sql, params)
        val endTime = (System.currentTimeMillis - startTime) / 1000.0
        val resultMap = 
            Map(
                "time" -> endTime,
                "dbObject" -> db.toString,
                "date" -> (new java.util.Date).toString, 
                "rows" -> result.length, 
                "data" -> {if(result.length > 0) result else Nil},
                "header" -> {if(result.length > 0) result.head.keys.toList else Nil}
            )

        out.println(JsonBuilder.toJson(resultMap))
    }

    override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {

        val out = resp.getWriter

        val sql = queryCode.decode(req.getParameter("q"))   
        val params = req.getParameterValues("p") match {
            case null => Array[String]()
            case x    => x
        }

        val response = queryCode.insertData(this.servletName, db, sql, params)

        out.println(JsonBuilder.toJson(Map("status" -> response)))
    }
}

object JsonBuilder {
    def toJson(data: Any): String = {
        data match {
            case Nil => "[]"
            case x: Map[_,_] => "{" + x.foldLeft("")((b,a) => b + ",\"" + a._1 + "\":" + toJson(a._2)).tail + "}"
            case x: List[_]  => "[" + x.foldLeft("")((b,a) => b + "," + toJson(a)).tail + "]"
            case x: String   => "\"" + x + "\""
            case x           => x.toString
        }
    }
}


class Jdbc(driver: String, jdbcString: String, user: String, pwd: String) {

    import annotation.tailrec
    import java.sql.{DriverManager, ResultSet}

    Class.forName(driver)
    private val conn = DriverManager.getConnection(jdbcString, user, pwd)
    System.err.println("DB open " + conn)

    val connectionString = jdbcString
    var sql: String = ""
    var result: List[Map[String, String]] = List()

    def createTable(sql: String): Unit =
        conn.createStatement.executeUpdate(sql)

    def insertBatch(table: String, col: List[String], data: Iterator[Map[String, String]]): String = {
        
        val colstr = col.mkString(",")
        val holders = (1 to col.length)
            .map(x => "?")
            .mkString(",")
        val sql = s"INSERT INTO $table ($colstr) VALUES ($holders)"
        this.sql = sql
        val insertStmt = this.conn.prepareStatement(sql)

        try {
            data.foreach(
                row => {
                    (1 to col.length).foreach(i => insertStmt.setString(i, row(col(i-1))))     
                    insertStmt.executeUpdate
                })
        } catch {
            case e if e.toString.contains("primary key violation") => return "Row already exists in the database."
            case e if e.toString.contains("Data conversion error") => return "Data cannot contain empty fields."
            case e:Throwable => return e.toString
        }
        return "success"
    }

    def insert(sql: String, params: Array[String]): String = {

        val insertStmt = this.conn.prepareStatement(sql)
        (1 to params.length).foreach(i => insertStmt.setString(i, params(i-1)))

        try {
            insertStmt.executeUpdate.toString
        } catch {
            case e if e.toString.contains("primary key violation") => return "Row already exists in the database."
            case e if e.toString.contains("Data conversion error") => return "Data cannot contain empty fields."
            case e:Throwable => return e.toString
        }
        return "success"
    }

    def select(sql: String, params: Array[String] = Array()): List[Map[String, String]] = {

        System.err.println("DB query " + conn + " : " + sql)
        this.sql = sql
        val selectStmt = this.conn.prepareStatement(sql)
        (1 to sql.count(_ == '?')).foreach(i => selectStmt.setString(i, params(i-1)))
        
        val resultSet = selectStmt.executeQuery
        val resultSetMetadata = resultSet.getMetaData

        @tailrec
        def getRows(resultSet: ResultSet, rows: List[Map[String, String]] = List()):
            List[Map[String, String]] = resultSet.next match {
            case true => 
                val row = (1 to resultSetMetadata.getColumnCount)
                    .map(x => (resultSetMetadata.getColumnLabel(x), resultSet.getString(x)))
                    .toMap
                getRows(resultSet, rows ++ List(row))
            case false => rows
        }
        this.result = getRows(resultSet)
        return this.result
    }

    def close: Unit = {
        this.conn.close()
        Console.err.println("DB closed " + conn)
    }
}

