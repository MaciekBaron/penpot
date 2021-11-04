;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) UXBOX Labs SL

(ns app.main.ui.onboarding.questions
  "External form for onboarding questions."
  (:require
   [app.config :as cf]
   [goog.events :as ev]
   [promesa.core :as p]
   [app.util.dom :as dom]
   [rumext.alpha :as mf]))

(defn load-arengu-sdk
  [container-ref]
  (letfn [(on-init []
            (let [container (mf/ref-val container-ref)]
              (p/then (.embed js/ArenguForms "163413696390559789" container)
                      (fn [res]
                        (js/console.log "KAKAKA" res)))))

          (on-submit-success [event]
            (js/console.log "on submit success" event))

          (on-next-step [event]
            (js/console.log "on next-step success" event))
          ]

    (let [script (dom/create-element "script")
          head   (unchecked-get js/document "head")
          lkey1  (ev/listen js/document "af-submitForm-success" on-submit-success)
          lkey2  (ev/listen js/document "af-nextStep" on-next-step)]

      (unchecked-set script "src" "https://sdk.arengu.com/forms.js")
      (unchecked-set script "onload" on-init)
      (dom/append-child! head script)

      (fn []
        (ev/unlistenByKey lkey1)
        (ev/unlistenByKey lkey2)))))

(mf/defc questions-modal
  [props]
  (let [container (mf/use-ref)]

    (mf/use-effect (partial load-arengu-sdk container))

    [:div.modal-wrapper.questions-form
     [:div.modal-overlay
      [:div.modal-container {:ref container}]]]))

(mf/defc questions
  [{:keys [profile]}]
  (let [form-id   cf/onboarding-form-id
        props     (:props profile)
        answered? (:onboarding-questions-answered props false)]
    (when (and form-id (not answered?))
      [:& questions-modal {:form-id form-id}])))



