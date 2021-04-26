jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "numeric-id-pre": function ( a ) {
        var str = "changeClick(";
        var startPos = a.indexOf(str) + str.length;
        var endPos = a.indexOf(")");
        str = a.substring(startPos, endPos);
        return parseInt( str );
    },

    "numeric-id-asc": function ( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },

    "numeric-id-desc": function ( a, b ) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} );