<html>
    <head>
        <title>Last $maximumSize$ Exceptions</title>
        <link rel="stylesheet" href="$base$utterlyidle/rendering/style.css" type="text/css"/>
        <style>
            table, td {
                text-align: left;
                vertical-align: top;
                border-collapse:collapse;
            }
            tbody tr {
                border-top: 1px black solid;
            }
            td {
                padding-top: 0.5em;
                padding-right: 2em;
                font-size: smaller;
                white-space: pre;
            }
            tr:nth-child(even) {
                background: #DDD;
            }
            tr:nth-child(odd) {
                background: #FFF;
            }
        </style>
    </head>
<body>
    <h1>Last $maximumSize$ Exceptions</h1>

    <table>
        <thead>
            <tr>
                <th>Date</th>
                <th>Request</th>
                <th>Exception</th>
            </tr>
        </thead>
        <tbody>
            $exceptions : { exception |
                <tr>
                    <td>$exception.date$</td>
                    <td>$exception.request$</td>
                    <td>$exception.exception$</td>
                </tr>
            }$
        </tbody>
    <table>

</body>
</html>