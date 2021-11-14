-- customer_money_transfer.lua
local customerKeyspace = "customer:"
local balanceField = "balance"
local fromCustomerId = customerKeyspace .. KEYS[1]
local toCustomerId = customerKeyspace .. KEYS[2]

local fromBalance = tonumber(redis.call("HGET", fromCustomerId, balanceField))
local toBalance = tonumber(redis.call("HGET", toCustomerId, balanceField))
local amount = tonumber(ARGV[1])

if fromBalance ~= nil and toBalance ~= nil and (fromBalance >= amount) then
    redis.call("HSET", fromCustomerId, balanceField, fromBalance - amount)
    redis.call("HSET", toCustomerId, balanceField, toBalance + amount)
    return true
end
return false
