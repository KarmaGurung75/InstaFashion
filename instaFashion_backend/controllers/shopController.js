const Shop = require("../models/Shop");
const Review = require("../models/Review");
const { success, failure } = require("../utils/message.js");
const cloudinary = require("../utils/cloudinary.js");

module.exports.add_shop = async function (req, res) {
  console.log("Your details:-->", req.body);
  try {
    const userId = req.user._id;
    if (req.files !== undefined) {
      const formImage = req.files.image;
      const imagePath = formImage.tempFilePath;
      if (
        formImage.mimetype == "image/png" ||
        formImage.mimetype == "image/jpg" ||
        formImage.mimetype == "image/jpeg"
      ) {
        const image = await cloudinary.upload_image(imagePath, userId);
        const restaurant = new Shop({
          user: userId,
          name: req.body.name,
          description: req.body.description,
          phone: req.body.phone,
          openingTime: req.body.openingTime,
          closingTime: req.body.closingTime,
          address: req.body.address,
          image: image,
        });
        const savedShop = await restaurant.save();
        res.json(success("New Shop with Image Added", savedShop));
      } else {
        res.json(failure("Must be png, jpg or jpeg"));
      }
    }
  } catch (error) {
    console.log(error);
    res.send(failure());
  }
  res.end();
};

module.exports.update_shop_cover_image = async function (req, res) {
  try {
    const formImage = req.files.image;
    const imagePath = formImage.tempFilePath;
    if (
      formImage.mimetype == "image/png" ||
      formImage.mimetype == "image/jpg" ||
      formImage.mimetype == "image/jpeg"
    ) {
      const _id = req.params.id;
      const coverImage = await cloudinary.upload_image(imagePath, _id);
      await Shop.updateOne({ _id }, { coverImage });
      res.json(success("Shop Cover Image Changed"));
    } else {
      res.json(failure("Must be png, jpg or jpeg"));
    }
  } catch (error) {
    console.log(error);
    res.json(failure());
  }
  res.end();
};
module.exports.edit_shop_without_image = async function (req, res) {
  try {
    await Shop.findOneAndUpdate(
      {
        _id: req.params.id,
      },
      {
        name: req.body.name,
        description: req.body.description,
        phone: req.body.phone,
        openingTime: req.body.openingTime,
        closingTime: req.body.closingTime,
        address: req.body.address,
      }
    );
    res.json(success("Updated Shop Details without image"));
  } catch (error) {
    console.log(error);
    res.send(failure());
  }
  res.end();
};
module.exports.update_shop_image = async function (req, res) {
  try {
    const formImage = req.files.image;
    const imagePath = formImage.tempFilePath;
    if (
      formImage.mimetype == "image/png" ||
      formImage.mimetype == "image/jpg" ||
      formImage.mimetype == "image/jpeg"
    ) {
      const _id = req.params.id;
      const image = await cloudinary.upload_image(imagePath, _id);
      await Shop.updateOne({ _id }, { image });
      res.json(success("Shop Image Changed"));
    } else {
      res.json(failure("Must be png, jpg or jpeg"));
    }
  } catch (error) {
    console.log(error);
    res.json(failure());
  }
  res.end();
};
module.exports.get_shop_details = async function (req, res) {
  try {
    const user = req.user;
    const restaurantId = req.params.id;
    let restaurant = await Shop.findById(restaurantId);
    if (restaurant) {
      const data = restaurant.toObject();
      data["review"] = await Review.find({ restaurant: restaurantId }).populate(
        "user"
      );
      res.json(success("Shop Found", data));
    } else {
      res.json(failure("Shop not found"));
    }
  } catch (error) {
    console.log(error);
    res.json(failure());
  }
  res.end();
};

module.exports.delete_shop = async function (req, res) {
  try {
    await Shop.deleteOne({ _id: req.params.id, user: req.user._id });
    res.json(success("Shop Deleted"));
  } catch (e) {
    console.log(e);
    res.json(failure());
  }
  res.end();
};
