# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20160923023052) do

  create_table "a", id: false, force: true do |t|
    t.integer "i", limit: 10
  end

  create_table "ar_internal_metadata", primary_key: "key", force: true do |t|
    t.string    "value"
    t.timestamp "created_at", limit: 29, null: false
    t.timestamp "updated_at", limit: 29, null: false
  end

  create_table "companies", force: true do |t|
    t.string    "name"
    t.timestamp "created_at", limit: 29
    t.timestamp "updated_at", limit: 29
  end

end
