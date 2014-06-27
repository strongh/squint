(ns squint.core
  (:import [java.awt.image BufferedImage]
           [java.awt Color])
  (:use [mikera.image core colours])
  (:require [clojure.java.io :refer [resource]]
            [hiphip.int :as hh])
  (:gen-class))


(defn mean
  ([x]
     (/ (apply + x)
        (count x)))
  ([x total]
     (/ (apply + x)
        total)))

(defn get-rgb
  ([img x y]
     (.getRGB img x y))
  ;; not sure this part is correct usage. :/
  ;; but simple tests seem to suggest so
  ([^BufferedImage img x y w h]
     (.getRGB img x y w h nil 0 w)))

(defn brightness
  "This is the W3C formula. See

http://www.nbdtech.com/Blog/archive/2008/04/27/Calculating-the-Perceived-Brightness-of-a-Color.aspx

Or

http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color"
  [^Color color]
  (let [r (.getRed color)
        g (.getGreen color)
        b (.getBlue color)]
    (float (/ (+ (* r 299)
                 (* g 587)
                 (* b 114))
              1000))))

(defn rgb-brightness
  [rgb]
  (brightness (color rgb)))

(defn brightness->character
  [b]
  (cond
   (> b 240) " "
   (> b 210) "'"
   (> b 200) "\""
   (> b 175) "*"
   (> b 150) "+"
   (> b 125) "%"
   (> b 100) "&"
   (> b 75) "@"
   (> b 1) "#"
   :else " "))

(defn boundary-fn
  "Returns a fn that will be used to avoid boundaries of the image
when taking averages of blocks of pixels. Used for both width and height."
  [edge]
  (fn [coord off]
    (min (- edge coord)
         off)))

(defn -main [file-name & [res & _]]
  (let [img-res (load-image file-name)
        spc (if res (Integer/parseInt res) 20)
        h-spc (/ spc 2)
        h (.getHeight img-res)
        w (.getWidth img-res)
        pixels (get-pixels img-res)
        ;; want more x's since characters tend to be ~2x as high as wide
        ;; this ratio varies by typeface but for us 2x is good enough.
        x-samples (map (partial * h-spc) (range (* 2 (/ w spc))))
        y-samples (map (partial * spc) (range (/ h spc)))
        last-x (last x-samples)
        h-bound (boundary-fn h)
        w-bound (boundary-fn w)]
    (spit (str file-name ".txt")
     (apply str
            (for [y y-samples
                  x x-samples]
              (let [br
                    (mean
                     (hh/amap [rgb (get-rgb img-res
                                            x y
                                            (w-bound x spc)
                                            (h-bound y h-spc))]
                              (rgb-brightness rgb)))
                    nl? (when (= x last-x) "\n")]
                (str (brightness->character br)
                     nl?)))))))
