<html>
<head>
    $response.head$
</head>
<body>
$response.body$
<div>
    <table class="zebra-striped">
        <thead>
        <tr>
            <th class="header">#</th>
            <th class="header">Path</th>
            <th class="header">Status Code</th>
            <th class="header">Execution Time (ms)</th>
        </tr>
        </thead>
        <tbody>
        $executionTimes : {
        <tr>
            <td>$i$</td>
            <td>$it.path$</td>
            <td>$it.statusCode$</td>
            <td>$it.executionTime$</td>
        </tr>
        }$
        </tbody>
    </table>
</div>
</body>
</html>