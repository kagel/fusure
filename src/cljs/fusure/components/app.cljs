(ns fusure.components.app
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [fusure.components.table :refer [tracks-table-view
                                             artists-table-view]]
            [fusure.components.search :refer [search-view]]
            [fusure.components.vk-login :refer [vk-login-view]]
            [fusure.components.lastfm-login :refer [lastfm-login-view]]
            [fusure.services.lastfm :refer [get-artist login]]
            [fusure.services.alchemy :refer [entities]]
            [cljs.core.async :refer [chan]]
            [fusure.services.vk :refer [audio-search]]))

(defn app-view [state]
  (reify
    om/IInitState
    (init-state [_]
      {:tracks-chan  (chan)
       :artists-chan (chan)})
    om/IRenderState
    (render-state [_ {:keys [tracks-chan artists-chan]}]
      (html
        [:div.col-lg-4
         (om/build vk-login-view state)
         (om/build lastfm-login-view state)
         (om/build search-view {:artists-chan artists-chan})
         (om/build artists-table-view {:artists-chan artists-chan
                                       :tracks-chan  tracks-chan})
         (om/build tracks-table-view {:state state :chan tracks-chan} )
         (get-artist "The Ex")
         (entities "The Ex is anadventurous, innovative band from Amsterdam, The Netherlands. They formed in 1979 at the height of the original punk explosion and have released over twenty full-length albums since, making them one of the longest-lived and most influential underground bands (along with The Fall) still in existence. The Ex just celebrated its 33⅓ year anniversary with a series of festivals. Not merely retrospective but primarily forward-looking and adventurous. The Ex have defied categorization ever since they started playing in 1979. Born out of the punk explosion, when anything and everything was possible, the band have still managed to retain both curiosity and passion for their music. Using guitars, bass, drums and voice as ther starting point The Ex have continued to musically explore undiscovered areas right up to the present day: the early 1980s saw collaborations with jazz musicians and an Iraqi-Kurdish band. In the 90s the group found a myriad of partners from varied musical and non-musical backgrounds including Kamagurka, Tom Cora, Sonic Youth, Han Bennink, Jan Mulder, Shellac and Wolter Wierbos. In 2002 The Ex set up a lively musical exchange with Ethiopia, which eventually led to two CD recordings and hundreds of concerts with the legendary saxophonist Getatchew Mekuria. After 33⅓ years, more than 25 albums and around 1800 performances the band continues to work as they did in when they began, completely independent of record companies, managers or roadies. Because of this ‘do it yourself’ work ethic The Ex is still a great example for other forward-thinking bands and musicians. The Ex debuted with a single titled “Stupid Americans” on the Utregpunx vinyl 7” compilation released by Rock Against records in Rotterdam. The release of their first 7” All Corpses Smell the Same came shortly thereafter in 1980. Through the decades they gradually developed into their current form of highly intricate, experimental punk/post-punk/no wave-inspired work. Always involved in a large number of projects, both in and outside the band, its members have been able to keep their music fresh and exciting, and, some opine, constantly better. Breaking from the relatively narrow confines of punk rock, The Ex has incorporated a wide array of influences, often from non-Western and non-rock sources. Some include hungarian and turkish folk songs, and more recently music from ethiopia (including collaborations with Ethipian saxophonist Gétatchèw Mèkurya, congo (shown in their tribute to Congolese street band Konono Nº1 and Eritrea (whose independence song is covered on Turn). Other examples of branching out stylistically include the improvised double album Instant and a release under the moniker Ex Orkest, a 20 piece big band assembled for performances at the esteemed Holland Festival. The band has had successful collaborations with many disparate artists, including UK anarchist band Chumbawamba (sometimes using the name Antidote), Dog Faced Hermans, and with the late avant-garde cellist Tom Cora in the early 1990s, resulting in the watershed album Scrabbling at the Lock in 1991 and the follow-up And the Weathermen Shrug Their Shoulders in 1993. They have also collaborated with members of Sonic Youth, Dutch improvisers ICP Orchestra, and released a collaborative EP with America’s Tortoise. In January 2009, front man and founding member G.W. Sok announced on the band’s website he will quit the band. Arnold de Boer from the Amsterdam band Zea will replace him.")]))))
