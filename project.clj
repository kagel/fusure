(defproject
  fusure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "The MIT License"
            :url  "http://opensource.org/licenses/MIT"}

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.189"]
                 [org.clojure/core.async "0.2.374"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [enlive "1.1.6"]
                 [org.omcljs/om "0.9.0"]
                 [environ "1.0.1"]
                 [reloaded.repl "0.2.1"]
                 [com.taoensso/timbre "4.1.4"]
                 [sablono "0.5.0"]
                 [ankha "0.1.5.1-479897"]
                 [aprint "0.1.3"]
                 [com.andrewmcveigh/cljs-time "0.3.14"]
                 [com.taoensso/sente "1.6.0"]
                 [com.taoensso/carmine "2.12.1"]
                 [cljsjs/fixed-data-table "0.4.6-0" :exclusions [cljsjs/react]]]

  :plugins [[lein-ring "0.8.13"]
            [lein-cljsbuild "1.0.5"]
            [lein-environ "1.0.0"]
            [lein-kibit "0.1.2"]
            [lein-ancient "0.6.3" :exclusions [org.clojure/clojure]]
            [lein-pprint "1.1.1" :exclusions [org.clojure/clojure]]]

  :min-lein-version "2.5.0"

  :uberjar-name "fusure.jar"

  :cljsbuild {:builds
              {:app
               {:source-paths
                ["src/cljs"]
                :compiler
                {:output-to     "resources/public/js/app.js"
                 :foreign-libs  [{:file     "resources/public/vendor/js/jquery-1.9.1.js"
                                  :file-min "resources/public/vendor/js/jquery-1.9.1.min.js"
                                  :provides ["cljsjs.jquery"]}
                                 {:file     "resources/public/vendor/js/bootstrap.js"
                                  :file-min "resources/public/vendor/js/bootstrap.min.js"
                                  :provides ["cljsjs.bootstrap"]
                                  :requires ["cljsjs.jquery"]}
                                 {:file     "resources/public/vendor/js/react-table.js"
                                  :provides ["cljsjs.react-table"]}
                                 {:file     "resources/public/vendor/js/vk.js"
                                  :provides ["cljsjs.vk"]}
                                 {:file     "resources/public/vendor/js/musicbrainz.js"
                                  :provides ["cljsjs.musicbrainz"]}
                                 {:file     "resources/public/vendor/js/lastfm.api.cache.js"
                                  :provides ["cljsjs.lastfm.cache"]}
                                 {:file     "resources/public/vendor/js/lastfm.api.md5.js"
                                  :provides ["cljsjs.lastfm.md5"]}
                                 {:file     "resources/public/vendor/js/lastfm.api.js"
                                  :provides ["cljsjs.lastfm.api"]
                                  :requires ["cljsjs.lastfm.md5"]}]
                 :externs       ["resources/public/vendor/js/jquery-1.9.0.ext.js"]
                 :optimizations :none
                 :pretty-print  true}}}}

  :profiles {:dev        {:repl-options {:timeout 600000}

                          :env          {:is-dev    true
                                         :http-port "4000"}

                          :cljsbuild    {:builds
                                         {:app
                                          {:source-paths
                                           ["env/dev/cljs"]
                                           :compiler
                                           {:output-dir "resources/public/js/out"
                                            :source-map "resources/public/js/out.js.map"}}}}
                          :injections   [(use 'aprint.core)]}

             :production {:auto-clean  false
                          :main        fusure.core
                          :aot         :all
                          :omit-source false
                          :env         {:production true
                                        :is-dev     false
                                        :http-port  "4000"}
                          :cljsbuild   {:builds
                                        {:app
                                         {:source-paths ["env/prod/cljs"]
                                          :compiler     {:optimizations :whitespace
                                                         :pretty-print  true}}}}}}
  :ring {:handler fusure.core/app}

  :main fusure.core

  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"])
