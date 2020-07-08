const sessionKey = 'keyword';

var setPlaceOnMap = function (lat, lng, keyword) {
    console.log("Rendering map...");

    var coordination = new kakao.maps.LatLng(lat, lng);

    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        mapOption = {
            center: coordination, // 지도의 중심좌표
            level: 3, // 지도의 확대 레벨
            draggable: false
        };

    // 지도를 표시할 div와  지도 옵션으로  지도를 생성합니다
    var map = new kakao.maps.Map(mapContainer, mapOption);

    // 마커를 생성합니다
    var marker = new kakao.maps.Marker({
        position: coordination
    });

    // 마커가 지도 위에 표시되도록 설정합니다
    marker.setMap(map);

    var iwContent = '<div style="padding:5px; text-overflow:ellipsis;">' + keyword + '</div>'; // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능합니다

    // 인포윈도우를 생성하고 지도에 표시합니다
    var infowindow = new kakao.maps.InfoWindow({
        map: map, // 인포윈도우가 표시될 지도
        position : coordination,
        content : iwContent,
        removable : false
    });
};

var getKeywordApi = function () {
    $.ajax({
         type : 'GET',
         url : '/api/keywords/hot',
         contentType : 'application/json; charset=utf-8'
    }).done(function (data, status, xhr) {
        console.log("keyword refresh");
        if (data.length == 0) {
            return;
        }

        let template = document.getElementById('hotKeywordItemTemplate').innerHTML;
        let rendered = Mustache.render(template, {'hotKeywords' : data});
        document.getElementById('hotKeywordList').innerHTML = rendered;
    }).fail(function (xhr, status, error) {
         console.log(status);
         console.log(error);
    })
};

var getPlaceDetailApi = function (placeId) {
    $.ajax({
         type : 'GET',
         url : '/api/places/' + placeId,
         contentType : 'application/json; charset=utf-8'
    }).done(function (data, status, xhr) {
        console.log("Get a detail of place");

        if (data == null) {
            alert("일시적인 문제로 실행할 수 없습니다.\n 잠시 후 다시 실행해주세요.");
        }

        let template = document.getElementById('placeDetailModal').innerHTML;
        let rendered = Mustache.render(template, data);
        document.getElementById('placeDetailModal').innerHTML = rendered;

        setPlaceOnMap(data.latitude, data.longitude, data.name);
    }).fail(function (xhr, status, error) {
         console.log(status);
         console.log(error);
    })
};

var getQueryParamsFromUrl = function () {
    var params = {};
    window.location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,
        function(str, key, value) {
            params[key] = value;
        });
    return params;
}

var setQueryAndSubmit = function (event, btnType) {
    event.preventDefault();

    let pageNo = 0;
    switch (btnType) {
        case 'prev':
            pageNo = event.target.dataset.pageno -1;
            break;
        case 'next':
            pageNo = event.target.dataset.pageno +1;
            break;
        case 'item':
            pageNo = event.target.dataset.pageno;
            break;
    }

    location.href = "?query=" + sessionStorage.getItem(sessionKey) +
                    "&page=" + pageNo;
}

document.addEventListener("DOMContentLoaded", function() {

    getKeywordApi();
    let intervalId = setInterval(getKeywordApi, 10000);

    if (sessionStorage.getItem(sessionKey == null)) {
        sessionStorage.setItem(sessionKey, decodeURI(getQueryParamsFromUrl().query));
    }

    document.getElementById("refreshHotKeyword").onclick = function(e) {
        e.preventDefault();
        getKeywordApi();
    }
    document.getElementById("stopRefreshHotKeyword").onclick = function(e) {
        console.log("interval stop (" + intervalId + ")");
        e.preventDefault();
        clearInterval(intervalId);
    }
    document.getElementById("searchForm").onsubmit = function(e) {
        sessionStorage.setItem(sessionKey, document.searchForm.query.value);
    }

    let btnPages = document.getElementsByClassName("btn-page");
    for (let i = 0; i < btnPages.length; i++) {
        btnPages[i].onclick = function(e) { setQueryAndSubmit(e, 'item'); }
    }
    let btnPrevPage = document.getElementById("btnPrevPage")
    if (btnPrevPage != null) {
        btnPrevPage.onclick = function(e) { setQueryAndSubmit(e, 'prev'); }
    }
    let btnNextPage = document.getElementById("btnNextPage")
    if (btnNextPage != null) {
        btnNextPage.onclick = function(e) { setQueryAndSubmit(e, 'next'); }
    }

    $('#placeDetailModal').on('shown.bs.modal', function (e) {
      let placeId = $(e.relatedTarget).data('placeid');

      getPlaceDetailApi(placeId);
    });

});