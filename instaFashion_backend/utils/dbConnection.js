const mongoose = require("mongoose");
require("dotenv").config();
mongoose.set("strictQuery", true);

const URI = process.env.MONGO_URL || "mongodb://127.0.0.1:27017/Insta-Draft";
module.exports = () => {
  console.log("Connecting to database");
  return mongoose.connect(URI);
};
