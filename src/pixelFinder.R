write(Sys.getpid(), "pixelFinderPID.txt")
library(tidyverse)
library(dplyr)
library(readr)

Sys.sleep(1)

pixelMap <- read.csv("pixelColourMap_test.csv") %>%
  as.data.frame()

definitionMap <- read.csv("definitions_example.csv") %>%
  as.data.frame()

tilemap <- left_join(definitionMap, pixelMap, by=c('Red', 'Green', 'Blue'))

provinceMap <- tilemap %>% 
  group_by(index) %>%
  summarise(
    avgX = mean(x),
    avgY = mean(y)
  ) %>%
  ungroup()

tilemap <- tilemap %>%
  select(index, x, y, Red, Green, Blue) %>%
  write_csv("provincesEnriched.csv")

provinceMap %>%
  select(index, avgX, avgY) %>%
  write.csv("provinceCenters.csv")
