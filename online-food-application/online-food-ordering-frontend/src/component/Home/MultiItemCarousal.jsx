import React from "react";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import Slider from "react-slick";
import { topMeels } from "../Data/topMeels";
import CarousalItem from "./CarousalItem";

const MultiItemCarousal = () => {
  return (
    <div>
      <Slider>
        {topMeels.map((item) => (
          <CarousalItem image={item.image} title={item.title} />
        ))}
      </Slider>
    </div>
  );
};

export default MultiItemCarousal;
