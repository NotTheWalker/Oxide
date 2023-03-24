write(Sys.getpid(), "pixelFinderPID.txt")
library(tidyverse)

Sys.sleep(1)

pixelMap <- read.csv("./input/pixelColourMap_test.csv") %>%
  as.data.frame()

definitionMap <- read.csv("./input/definitions_example.csv") %>%
  as.data.frame()

tilemap <- left_join(definitionMap, pixelMap, by=c('Red', 'Green', 'Blue'))

tilemap = tilemap[-1,]

provinceMap <- tilemap %>% 
  group_by(index) %>%
  summarise(
    avgX = round(mean(x)),
    avgY = round(mean(y)),
    maxX = max(x),
    minX = min(x),
    maxY = max(y),
    minY = min(y)
  ) %>%
  ungroup()

tilemap <- tilemap %>%
  select(index, x, y, Red, Green, Blue) %>%
  write_csv("./input/provincesEnriched.csv")

provinceMap %>%
  select(index, avgX, avgY, maxX, minX, maxY, minY) %>%
  write.csv("./input/provinceShape.csv")
