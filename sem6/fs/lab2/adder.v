`timescale 1ns / 1ps

module adder(
    input wire [15:0] a,
    input wire [15:0] b,

    output wire [15:0] res
);

assign res = a + b;


endmodule