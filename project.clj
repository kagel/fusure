(defproject
  fusure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "The MIT License"
            :url  "http://opensource.org/licenses/MIT"}

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [compojure "1.3.2"]
                 [enlive "1.1.5"]
                 [org.omcljs/om "0.8.8"]
                 [environ "1.0.0"]
                 [reloaded.repl "0.1.0"]
                 [com.taoensso/timbre "3.4.0"]
                 [sablono "0.3.4"]
                 [ankha "0.1.5.1-479897"]
                 [aprint "0.1.3"]
                 [com.andrewmcveigh/cljs-time "0.3.2"]
                 [com.taoensso/sente "1.4.1"]
                 [com.taoensso/carmine "2.9.2"]]

  :plugins [[lein-ring "0.8.13"]
            [lein-cljsbuild "1.0.5"]
            [lein-environ "1.0.0"]
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
                                  :requires ["cljsjs.jquery"]}]
                 :externs       ["resources/public/vendor/js/jquery-1.9.0.ext.js"]
                 :optimizations :none
                 :pretty-print  true}}}}

  :profiles {:dev     {:repl-options {:timeout 600000}

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

             :uberjar {:auto-clean  false
                       :main        fusure.core
                       :aot         [fusure.core]
                       :omit-source false
                       :env         {:production true
                                     :is-dev     false
                                     :http-port  "4000"}
                       :cljsbuild   {:builds
                                     {:app
                                      {:source-paths ["env/prod/cljs"]
                                       :compiler     {:optimizations :advanced
                                                      :pretty-print  false}}}}}}
  :ring {:handler fusure.core/app}

  :main        fusure.core

  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"])
