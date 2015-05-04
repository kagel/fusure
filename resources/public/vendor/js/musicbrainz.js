var MBz = (function () {
    "use strict";

    // Helper function to get data from the MusicBrainz API
    // TODO: enforce rate limiting http://musicbrainz.org/doc/XML_Web_Service%2FRate_Limiting
    var get = function (query, callback) {
        query = 'http://www.musicbrainz.org/ws/2/' + query;
        $.ajax(query, {
            "dataType": 'xml',
            // #TODO: figure out how i should be setting headers
            "success": callback,
            "failure": function () {
                //stat.show 'Unable to conect to the MusicBrainz servers', 5000;
            }
        });
    };

    var set = function (context, key, value) {
        if (value !== "") {
            context[key] = value;
        }
    };

    // https://wiki.musicbrainz.org/Label
    var Label = function (xml) {
        var $xml = $(xml),
            that = this;

        // MusicBrainz ID of the label
        set(this, "mbid", $xml.attr("id"));

        // Describes the main activity of the label
        set(this, "type", $xml.attr("type"));

        // Official name of the label
        set(this, "name", $xml.children("name").text());

        // Variant of the name used when sorting labels by name
        set(this, "sortName", $xml.children("sort-name").text());

        // Country of origin for the label
        set(this, "country", $xml.children("country").text());

        // Helps distinguish between identically named labels
        set(this, "disambig", $xml.children("disambiguation").text());

        // Life span of the label
        // More info: https://wiki.musicbrainz.org/Begin_and_End_Dates
        this.lifeSpan = {};

        set(this.lifeSpan, "begin", $xml.children("life-span").children("begin").text());

        set(this.lifeSpan, "end", $xml.children("life-span").children("end").text());

        if ($xml.children("life-span").children("ended").text() === "true") {
            this.lifeSpan.ended = true;
        } else {
            this.lifeSpan.ended = false;
        }

        // Commmon names the label also goes by
        if ($xml.children("alias-list").children().length > 0) {
            this.aliases = [];
            $xml.children("alias-list").children().each(function () {
                that.aliases.push($(this).text());
            });
        }

        // Tags associated with the label
        if ($xml.children("tag-list").children().length > 0) {
            this.tags = {};
            $xml.children("tag-list").children().each(function () {
                var $this = $(this),
                    name = $this.children("name").text(),
                    count = $this.attr("count");
                that.tags[name] = count;
            });
        }

        // TODO: Store the values from "medium-list"
        // TODO: Store the values from "text-representation"
    };


    // https://wiki.musicbrainz.org/Release_Group
    var ReleaseGroup = function (xml) {
    };


    // https://wiki.musicbrainz.org/Release
    var Release = function (xml) {
        var that = this,
            $xml = $(xml);

        // MusicBrainz ID of the release
        if ($xml.attr("id") !== "") {
            this.mbid = $xml.attr("id");
        }

        // Title of the release
        if ($xml.children("title").text() !== "") {
            this.title = $xml.children("title").text();
        }

        // Date the release was issued
        if ($xml.children("date").text() !== "") {
            this.date = $xml.children("date").text();
        }

        // Country the release was issued in
        if ($xml.children("country").text() !== "") {
            this.country = $xml.children("country").text();
        }

        // Describes how "official" a release is
        if ($xml.children("status").text() !== "") {
            this.status = $xml.children("status").text();
        }

        // ASIN of the release
        // https://wiki.musicbrainz.org/ASIN
        if ($xml.children("asin").text() !== "") {
            this.asin = $xml.children("asin").text();
        }

        // Barcode of the release
        if ($xml.children("barcode").text() !== "") {
            this.barcode = $xml.children("barcode").text();
        }

        // Artist(s) that the release is primarily credited to
        if ($xml.children("artist-credit").children().length > 0) {
            this.artists = [];
            $xml.children("artist-credit").children().each(function () {
                var $this = $(this).children("artist"),
                    artist = {};

                // MusicBrainz ID of the artist
                if ($this.attr("id") !== "") {
                    artist.mbid = $this.attr("id");
                }

                // Official name of the artist
                if ($this.children("name").text() !== "") {
                    artist.name = $this.children("name").text();
                }

                // Variant of the name used when sorting artists by name
                if ($this.children("sort-name").text() !== "") {
                    artist.sortName = $this.children("sort-name").text();
                }

                // Helps distinguish between identically named artists
                if ($this.children("disambiguation").text() !== "") {
                    artist.disambig = $this.children("disambiguation").text();
                }

                that.artists.push(artist);
            });
        }

        // Label(s) the release was issued under
        if ($xml.children("label-info-list").children("label-info").length > 0) {
            this.labels = [];
            $xml.children("label-info-list").children("label-info").each(function () {
                var $this = $(this),
                    label = {};

                // MusicBrainz ID of the label the release was issued under
                if ($this.children("label").attr("id") !== "") {
                    label.mbid = $this.children("label").attr("id");
                }

                // Catalog number of the release issued under the label
                if ($this.children("catalog-number").text() !== "") {
                    label.catalogNum = $this.children("catalog-number").text();
                }

                // Name of the label the release was issued under
                if ($this.children("label").children("name").text() !== "") {
                    label.name = $this.children("label").children("name").text();
                }

                that.labels.push(label);
            });
        }

        // 
        this.releaseGroup = {};

        // 
        if ($xml.children("release-group").attr("id") !== "") {
            this.releaseGroup.mbid = $xml.children("release-group").attr("id");
        }

        //
        if ($xml.children("release-group").children("primary-type").text() !== "") {
            this.releaseGroup.type = $xml.children("release-group").children("primary-type").text();
        }

        //
        if ($xml.children("release-group").find("secondary-type").length > 0) {
            this.releaseGroup.secondaryTypes = [];
            $xml.children("release-group").find("secondary-type").each(function () {
                that.releaseGroup.secondaryTypes.push($(this).text());
            });
        }
    };

    Release.prototype.getArtists = function (callback) {
        var url = "artist?release=" + this.mbid;
        get(url, function (data) {
            var $elems, artists;
            $elems = $(data).find('artist-list').children();
            artists = $.map($elems, function (elem) { return new Artist(elem); });
            callback(artists);
        });
    };

    Release.prototype.getReleaseGroup = function (callback) {
    };

    Release.prototype.getLabels = function (callback) {
    };


    // https://wiki.musicbrainz.org/Artist
    var Artist = function (xml) {
        var $xml = $(xml),
            that = this;

        // MusicBrainz ID of the artist
        if ($xml.attr('id') !== "") {
            this.mbid = $xml.attr('id');
        }

        // Official name of the artist
        if ($xml.children('name').text() !== "") {
            this.name = $xml.children('name').text();
        }

        // Variant of the name used when sorting artists by name
        if ($xml.children('sort-name').text() !== "") {
            this.sortName = $xml.children('sort-name').text();
        }

        // Whether the artist is a person or group
        if ($xml.attr("type") !== "") {
            this.type = $xml.attr("type");
        }

        // Stores the country with which an artist is primarily identified with
        if ($xml.children('country').text() !== "") {
            this.country = $xml.children('country').text();
        }

        // Helps distinguish between identically named artists
        if ($xml.children('disambiguation').text() !== "") {
            this.disambig = $xml.children('disambiguation').text();
        }

        // Whether a person identifies as male, female or neither
        if ($xml.children('gender').text() !== "") {
            this.gender = $xml.children('gender').text();
        }

        // Life span of the artist
        // More info: https://wiki.musicbrainz.org/Begin_and_End_Dates
        this.lifeSpan = {};

        if ($xml.children("life-span").children("begin").text() !== "") {
            this.lifeSpan.begin = $xml.children("life-span").children("begin").text();
        }

        if ($xml.children("life-span").children("end").text() !== "") {
            this.lifeSpan.end = $xml.children("life-span").children("end").text();
        }

        if ($xml.children("life-span").children("ended").text() === "true") {
            this.lifeSpan.ended = true;
        } else {
            this.lifeSpan.ended = false;
        }

        // If the artist has any IPI codes
        // https://wiki.musicbrainz.org/IPI
        if ($xml.children("ipi-list").children().length) {
            this.ipiCodes = [];
            $xml.children("ipi-list").children().each(function () {
                that.ipiCodes.push($(this).text());
            });
        }

        // Commmon names the artist also goes by
        if ($xml.children("alias-list").children().length) {
            this.aliases = [];
            $xml.children("alias-list").children().each(function () {
                that.aliases.push($(this).text());
            });
        }

        // Tags associated with the artist
        if ($xml.children("tag-list").children().length) {
            this.tags = {};
            $xml.children("tag-list").children().each(function () {
                var $this = $(this),
                    name = $this.children("name").text(),
                    count = $this.attr("count");
                that.tags[name] = count;
            });
        }
    };

    Artist.prototype.getReleases = function (callback) {
    };

    Artist.prototype.getRelGroups = function (callback) {
    };


    var searchResources = ["artist", "label", "recording", "release", "release-group", "work"];

    var ret = {};

    ret.search = function (opts) {
        var url,
            query = encodeURIComponent(opts.query),
            resource = opts.resource,
            results = opts.results,
            limit = opts.limit || 10;


        if (searchResources.indexOf(resource.toLowerCase()) === -1) {
            console.error("'" + resource + "' is not a valid search resource.");
            return;
        }

        url = resource + "?query=" + query + "&limit=" + limit;
        get(url, function (data) {
            var $elems, resources;

            switch (resource) {
                case "artist":
                    $elems = $(data).find('artist-list').children();
                    resources = $.map($elems, function (elem) { return new Artist(elem); });
                    break;
                case "label":
                    $elems = $(data).find('label-list').children();
                    resources = $.map($elems, function (elem) { return new Label(elem); });
                    break;
                case "recording":
                    break;
                case "release":
                    $elems = $(data).find('release-list').children();
                    resources = $.map($elems, function (elem) { return new Release(elem); });
                    break;
                case "release-group":
                    break;
                case "work":
                    break;
            }

            results(resources);
        });
    };

    return ret;
}());